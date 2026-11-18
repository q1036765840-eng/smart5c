package com.nbk.smart5c.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.nbk.smart5c.client.AiAlarmClient;
import com.nbk.smart5c.client.AiClient;
import com.nbk.smart5c.common.constants.SystemConstants;
import com.nbk.smart5c.common.enums.*;
import com.nbk.smart5c.common.utils.*;
import com.nbk.smart5c.common.websocket.WebSocketMsg;
import com.nbk.smart5c.common.websocket.WebSocketSessionManager;
import com.nbk.smart5c.entity.*;
import com.nbk.smart5c.entity.Task;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.mapper.*;
import com.nbk.smart5c.pojo.bo.*;
import com.nbk.smart5c.pojo.qo.InspectionDataQO;
import com.nbk.smart5c.pojo.vo.*;
import com.nbk.smart5c.service.DeviceService;
import com.nbk.smart5c.service.InspectionDataService;
import com.nbk.smart5c.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据表业务逻辑层
 * @since 2026-01-27 15:25:38
 */
@Log4j2
@Service
@Primary
@RequiredArgsConstructor
public class InspectionDataServiceImpl extends ServiceImpl<InspectionDataMapper, InspectionData> implements InspectionDataService<InspectionDataMapper, InspectionData> {
    @Value("${inspect.basepath}")
    private String inspectDataPath;
    @Value("${inspect.host}")
    private String inspectHost;
    @Value("${ai.line_location_category}")
    private String lineCategory;
    private final AiClient aiClient;
    private final SectionMapper sectionMapper;
    private final LineMapper lineMapper;
    private final StationMapper stationMapper;
    private final DeviceMapper deviceMapper;
    private final InspectionReviewResultMapper inspectionReviewResultMapper;
    private final DeviceService<DeviceMapper, Device> deviceService;
    private final ApiUtils apiUtils;
    private final ExcelProcessUtil excelProcessUtil;
    private final TaskMapper taskMapper;
    private final ExportInfoMapper exportInfoMapper;
    private final AiAlarmClient aiAlarmClient;
    @Value("${jwt.tokenHeader:Authorization}")
    private String authTokenKey;
    @Value("${file-sync.access-token:__C5_OUTACCESS_5C__}")
    private String outAccessToken;
    @Value("${file-sync.host}")
    private String fileSyncHost;

    @Lazy
    private final TaskService<TaskMapper, Task> taskService;
    @Autowired
    @Qualifier("taskExecutor")
    private final ScheduledExecutorService taskScheduler;
    private final HttpServletRequest request;

    // 线程池配置
    private final ExecutorService executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2
    );

    // 缓存映射
    private final Map<String, Long> sectionCache = new ConcurrentHashMap<>();
    private final Map<String, Long> lineCache = new ConcurrentHashMap<>();
    private final Map<String, Long> stationCache = new ConcurrentHashMap<>();
    private final Map<String, Long> deviceCache = new ConcurrentHashMap<>();

    // 批量处理大小
    private static final int BATCH_SIZE = 1000;

    // 并发控制
    private final ReentrantLock reconnectLock = new ReentrantLock();
    // 进度缓存（请求线程与推送解析线程池并发读写，需线程安全）
    public static final Map<String, String> processMap = new ConcurrentHashMap<>();
    // 每个用户最近一次查询时InspectionData表的最大id缓存
    public static final Map<String, Long> userMaxInspectionIdCache = new ConcurrentHashMap<>();
    //文件解析进度常数parseProcess_
    private static final String PROCESS_CONSTANT_PARSE = "parseProcess_";
    //算法分析进度常数analysisProcess_
    private static final String PROCESS_CONSTANT_ANALYSIS = "analysisProcess_";
    //算法分析进度常数analysisProcess_
    private static final String PROCESS_IMAGE_VALID = "imageValidProcess_";

    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(InspectionDataSaveBO info) {
        InspectionData entity = new InspectionData();
        BeanUtils.copyProperties(info, entity);
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public InspectionData queryById(String id) {
        return super.getById(id);
    }

    private LambdaQueryWrapper<InspectionData> buildQueryWrapper(InspectionDataQO query) {
        LambdaQueryWrapper<InspectionData> inspectQueryWrapper = new LambdaQueryWrapper<>();
        inspectQueryWrapper.eq(ObjectUtil.isNotEmpty(query.getSectionId()), InspectionData::getSectionId, query.getSectionId());
        inspectQueryWrapper.eq(ObjectUtil.isNotEmpty(query.getLineId()), InspectionData::getLineId, query.getLineId());
        inspectQueryWrapper.eq(ObjectUtil.isNotEmpty(query.getStationId()), InspectionData::getStationId, query.getStationId());
        inspectQueryWrapper.like(ObjectUtil.isNotEmpty(query.getLocomotiveNumber()), InspectionData::getLocomotiveNumber, query.getLocomotiveNumber());
        inspectQueryWrapper.eq(ObjectUtil.isNotEmpty(query.getConfirmStatus()), InspectionData::getConfirmStatus, query.getConfirmStatus());
        inspectQueryWrapper.eq(ObjectUtil.isNotEmpty(query.getAnalysisStatus()), InspectionData::getAnalysisStatus, query.getAnalysisStatus());
        inspectQueryWrapper.ge(ObjectUtil.isNotEmpty(query.getStartTime()), InspectionData::getInspectionTime, query.getStartTime() + " 00:00:00");
        inspectQueryWrapper.le(ObjectUtil.isNotEmpty(query.getEndTime()), InspectionData::getInspectionTime, query.getEndTime() + " 23:59:59");
        if (ObjectUtil.isNotNull(query.getTreeId())) {
            if (TreeTypeEnum.SECTION.getCode().equals(query.getTreeType())) {
                inspectQueryWrapper.in(InspectionData::getSectionId, query.getTreeId());
            } else if (TreeTypeEnum.LINE.getCode().equals(query.getTreeType())) {
                inspectQueryWrapper.in(InspectionData::getLineId, query.getTreeId());
            } else if (TreeTypeEnum.STATION.getCode().equals(query.getTreeType())) {
                inspectQueryWrapper.in(InspectionData::getStationId, query.getTreeId());
            } else if (TreeTypeEnum.PILLAR.getCode().equals(query.getTreeType())) {
                inspectQueryWrapper.in(InspectionData::getDeviceId, query.getTreeId());
            }
        }
        // 缓存当前用户对应的InspectionData表最大的id
        Long userId = null==apiUtils.currentUserId()?-1:apiUtils.currentUserId();
        String clientIP = HttpClientUtils.getClientIP(request);
        log.info("后端缓存ip:{}", clientIP);
        String userCacheKey = userId+"_"+clientIP;
        Long maxId = super.baseMapper.getMaxId();
        if (LogicStatusEnum.YES.getCode().equals(query.getRefreshFlag())) {
            //刷新数据
            if (null != maxId) {
                userMaxInspectionIdCache.put(userCacheKey, maxId);
            }
            inspectQueryWrapper = new LambdaQueryWrapper<>();
        } else {
            Long cachedMaxId = userMaxInspectionIdCache.get(userCacheKey);
            maxId = null == cachedMaxId ? maxId : cachedMaxId;
            if (null != maxId) {
                userMaxInspectionIdCache.put(userCacheKey, maxId);
            }
        }
        // 表为空时 maxId 为 null，不能对 id 做上界过滤（ConcurrentHashMap 也不允许 put null）
        inspectQueryWrapper.le(null != maxId, InspectionData::getId, maxId);
        // 关联任务表，按是否实时任务过滤
        inspectQueryWrapper.isNull(InspectionData::getTaskId);
        //受电工状态（可多选）：正常按主表状态过滤，异物/弯曲/崩块/缺失按审核结果标记的缺陷类型过滤，多选取并集
        if (ObjectUtil.isNotEmpty(query.getPantographStatus()) && !query.isPantographAll()) {
            List<Object> params = new ArrayList<>();
            List<String> defectConditions = new ArrayList<>();
            if (query.isPantographNormal()) {
                defectConditions.add("nbk_inspection_data.pantograph_status = " + PantographStatusEnum.NORMAL.getCode());
            }
            List<String> defectTypes = query.getPantographDefectTypes();
            if (CollUtil.isNotEmpty(defectTypes)) {
                //marked_regions 形如 [{"coord":"1,1,1,1","defectType":"弯曲","source":1}]，按缺陷类型中文名匹配。
                //所有缺陷类型合并为一个 IN 半连接，避免每种类型一个相关子查询导致主表逐行回表；
                //CAST(... AS CHAR) 兼容 MySQL json 与 KingbaseES jsonb 的 LIKE 比较
                List<String> likes = new ArrayList<>(defectTypes.size());
                for (String defectType : defectTypes) {
                    likes.add("CAST(rr.marked_regions AS CHAR) LIKE CONCAT('%', {" + params.size() + "}, '%')");
                    params.add(defectType);
                }
                defectConditions.add("nbk_inspection_data.id IN (SELECT rr.inspection_id FROM nbk_inspection_review_result rr"
                        + " WHERE rr.pantograph_status = " + InspectionData.PANTOGRAPH_STATUS_ERROR
                        + " AND (" + String.join(" OR ", likes) + "))");
            }
            inspectQueryWrapper.apply(defectConditions.isEmpty() ? "1 = 0" : "(" + String.join(" OR ", defectConditions) + ")",
                    params.toArray());
        }
        inspectQueryWrapper.in(InspectionData::getAnalysisStatus, AnalyzeStatusEnum.REVIEWED.getCode(), AnalyzeStatusEnum.WAIT_REVIEW.getCode());
        // inspection_time 存在大量重复值，仅按它排序时 OceanBase 等分布式库在翻页之间无法保证同一顺序，
        // 会出现跨页重复/漏行；补 id 作为唯一兜底排序键
        inspectQueryWrapper.last("order by inspection_time desc, id desc");
        return inspectQueryWrapper;
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<InspectionData> queryAndPaging(InspectionDataQO query) {
        LambdaQueryWrapper<InspectionData> inspectQueryWrapper = buildQueryWrapper(query);
        Page<InspectionData> page = super.page(new Page<>(query.getPageNo(), query.getPageSize()), inspectQueryWrapper);
        List<InspectionData> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            return page;
        }
        // 1. 批量收集所有关联ID
        Set<Long> sectionIds = records.stream().map(InspectionData::getSectionId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> lineIds = records.stream().map(InspectionData::getLineId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> stationIds = records.stream().map(InspectionData::getStationId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> deviceIds = records.stream().map(InspectionData::getDeviceId).filter(Objects::nonNull).collect(Collectors.toSet());

        // 2. 批量查询关联数据
        Map<Long, Section> sectionMap = sectionMapper.selectBatchIds(sectionIds).stream().collect(Collectors.toMap(Section::getId, s -> s));
        Map<Long, Line> lineMap = lineMapper.selectBatchIds(lineIds).stream().collect(Collectors.toMap(Line::getId, l -> l));
        Map<Long, Station> stationMap = stationMapper.selectBatchIds(stationIds).stream().collect(Collectors.toMap(Station::getId, s -> s));
        Map<Long, Device> deviceMap = deviceService.listByIds(deviceIds).stream().collect(Collectors.toMap(Device::getId, d -> d));
        Map<Long, String> defectTypeMap = queryDefectTypes(records.stream().map(InspectionData::getId).collect(Collectors.toSet()));

        // 3. 在内存中匹配数据，避免循环内单次查询
        // 4. 批量查询审核结果，按优先级取一条：受电弓异常(2) > 正常(1)
        Set<Long> inspectionIds = records.stream().map(InspectionData::getId).collect(Collectors.toSet());
        List<InspectionReviewResult> reviewResults = inspectionReviewResultMapper.selectList(
                new QueryWrapper<InspectionReviewResult>()
                        .in("inspection_id", inspectionIds)
                        .orderByDesc("pantograph_status")
                        .orderByAsc("id"));
        Map<Long, InspectionReviewResult> reviewResultMap = new HashMap<>(reviewResults.size());
        for (InspectionReviewResult reviewResult : reviewResults) {
            // 排序后异常(2)在前，仅保留每条巡检的第一条记录，即优先异常、同状态取id最小
            reviewResultMap.putIfAbsent(reviewResult.getInspectionId(), reviewResult);
        }
        for (InspectionData inspectionData : records) {
            inspectionData.setSectionName(sectionMap.getOrDefault(inspectionData.getSectionId(), new Section()).getSectionName());
            inspectionData.setLineName(lineMap.getOrDefault(inspectionData.getLineId(), new Line()).getLineName());
            inspectionData.setStationName(stationMap.getOrDefault(inspectionData.getStationId(), new Station()).getStationName());
            // 从 nbk_inspection_review_result 的 marked_regions 汇总缺陷类型
            inspectionData.setDefectType(StrUtil.isBlank(defectTypeMap.get(inspectionData.getId()))?"正常":defectTypeMap.get(inspectionData.getId()));
            // 优先取受电弓异常的记录，没有异常则取正常记录，回填标记区域与图片链接
            InspectionReviewResult inspectionReviewResult = reviewResultMap.get(inspectionData.getId());
            inspectionData.setMarkedRegions(null == inspectionReviewResult || CharSequenceUtil.isBlank(inspectionReviewResult.getMarkedRegions())
                    ? new ArrayList<>() : JSONUtil.toList(inspectionReviewResult.getMarkedRegions(), DefectMarkBO.class))
                    .setLocomotiveImage(null == inspectionReviewResult ? inspectionData.getLocomotiveImage() : inspectionReviewResult.getNetPath());

            if(null!=inspectionData.getTaskId()){
                Task task = taskMapper.selectById(inspectionData.getTaskId());
                inspectionData.setTaskName(null == task ? "" : task.getTaskName());
            }
            Device device = deviceMap.get(inspectionData.getDeviceId());
            if (null != device) {
                inspectionData.setDeviceCode(device.getDeviceCode()).setPillarNo(device.getPillarNo());
            }
        }
        return page;
    }

    /**
     * 批量汇总各巡检数据在审核结果 marked_regions 中标记的缺陷类型，多个类型用顿号拼接。
     *
     * @param inspectionIds 巡检数据id集合
     * @return inspectionId -> 缺陷类型，无缺陷的数据不在结果中
     */
    private Map<Long, String> queryDefectTypes(Set<Long> inspectionIds) {
        if (CollUtil.isEmpty(inspectionIds)) {
            return Collections.emptyMap();
        }
        // 误报记录在 misreport() 中已被置为受电弓正常，按 pantograph_status=2 过滤即可自然排除
        LambdaQueryWrapper<InspectionReviewResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(InspectionReviewResult::getInspectionId, InspectionReviewResult::getMarkedRegions)
                .in(InspectionReviewResult::getInspectionId, inspectionIds)
                .eq(InspectionReviewResult::getPantographStatus, InspectionData.PANTOGRAPH_STATUS_ERROR);
        List<InspectionReviewResult> reviewResults = inspectionReviewResultMapper.selectList(wrapper);
        if (CollUtil.isEmpty(reviewResults)) {
            return Collections.emptyMap();
        }
        Map<Long, Set<String>> typeMap = new LinkedHashMap<>();
        for (InspectionReviewResult reviewResult : reviewResults) {
            for (DefectMarkBO mark : parseMarkedRegions(reviewResult.getMarkedRegions())) {
                if (null == mark || CharSequenceUtil.isBlank(mark.getDefectType())) {
                    continue;
                }
                typeMap.computeIfAbsent(reviewResult.getInspectionId(), k -> new LinkedHashSet<>())
                        .add(mark.getDefectType().trim());
            }
        }
        Map<Long, String> defectTypeMap = new HashMap<>(typeMap.size());
        typeMap.forEach((inspectionId, types) -> defectTypeMap.put(inspectionId, String.join("、", types)));
        return defectTypeMap;
    }

    /**
     * 解析 marked_regions JSON 字符串为标记区域列表；空串或解析失败返回空列表，避免单条脏数据拖垮整个复核。
     */
    private List<DefectMarkBO> parseMarkedRegions(String markedRegions) {
        if (CharSequenceUtil.isBlank(markedRegions)) {
            return new ArrayList<>();
        }
        try {
            return JSONUtil.toList(markedRegions, DefectMarkBO.class);
        } catch (Exception e) {
            log.warn("解析标记区域失败，markedRegions: {}", markedRegions, e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<InspectionDataVO> queryAll(InspectionDataQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(InspectionDataUpdateBO info) {
        InspectionData entity = new InspectionData();
        BeanUtils.copyProperties(info, entity);
        return super.updateById(entity);
    }

    /**
     * 数据导出
     *
     * @param query 查询条件
     */
    @Override
    public void export(InspectionDataQO query, HttpServletResponse response) {
        // 查询用户数据
        List<InspectionDataVO> result = baseMapper.queryAll(query);
        List<List<String>> data = new ArrayList<>(result.size());
        for (InspectionDataVO item : result) {
            data.add(Arrays.asList(
                    item.getId() + "",
                    item.getDeviceId() + "",
                    item.getSectionId() + "",
                    item.getLineId() + "",
                    item.getStationId() + "",
                    Objects.isNull(item.getInspectionTime()) ? "" : item.getInspectionTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    item.getLocomotiveNumber(),
                    item.getAnalysisStatus() + "",
                    item.getKmSign(),
                    item.getPantographStatus() + "",
                    item.getLocomotiveImage(),
                    item.getCreatedUser(),
                    Objects.isNull(item.getCreatedTime()) ? "" : item.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    item.getUpdatedUser(),
                    Objects.isNull(item.getUpdatedTime()) ? "" : item.getUpdatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    /**
     * 模板导出
     */
    @Override
    public void exportTemplate(HttpServletResponse response) {

    }

    /**
     * 数据导入
     */
    @Override
    public void analysis(MultipartFile file) {

    }

    @Override
    public List<InspectionReviewDataVO> inspectionDataList(Long deviceId) {
        return super.baseMapper.inspectionDataList(deviceId);
    }

    /**
     * 解析文件夹下的数据
     *
     * @param filePath 解析目录
     * @return
     */
    @Override
    public Boolean parseData(Long taskId, String filePath) throws ServiceException {
        File dataFile = new File(filePath);
        if (!dataFile.exists()) {
            throw new ServiceException(ResultCode.FILE_PARSE_FAIL.getCode(), "解析失败：服务器上无此文件路径");
        }

        // 更新进度
//        processMap.put(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId(), "0%");

        // 清空缓存
        clearCache();

        // 获取待处理的目录列表
        List<File> fileList = getDirectoriesWithFiles(dataFile);

        if (CollUtil.isEmpty(fileList)) {
            return true;
        }

        // 计算总目录数
        AtomicInteger totalDirs = new AtomicInteger(fileList.size());
        AtomicInteger processedDirs = new AtomicInteger(0);

        // 处理所有文件目录
        for (File file : fileList) {
            try {
                processFileFolderParse(taskId, file, processedDirs, totalDirs);
            } catch (Exception e) {
                throw new ServiceException(ResultCode.FILE_PARSE_FAIL.getCode(), "文件夹解析失败");
            }
        }
        return true;
    }


    /**
     * 解析文件夹下的数据
     *
     * @param filePath 解析目录
     * @return
     */
    @Override
    public Boolean parseDataNoTask(String filePath) throws ServiceException {
        File dataFile = new File(filePath);
        if (!dataFile.exists()) {
            throw new ServiceException(ResultCode.FILE_PARSE_FAIL.getCode(), "解析失败：服务器上无此文件路径");
        }


        // 清空缓存
        clearCache();

        // 获取待处理的目录列表
        List<File> fileList = getDirectoriesWithFiles(dataFile);

        if (CollUtil.isEmpty(fileList)) {
            return true;
        }

        // 计算总目录数
        AtomicInteger totalDirs = new AtomicInteger(fileList.size());
        AtomicInteger processedDirs = new AtomicInteger(0);

        // 处理所有文件目录
        for (File file : fileList) {
            try {
                processFileFolderParseNoTask(file, processedDirs, totalDirs);
            } catch (Exception e) {
                throw new ServiceException(ResultCode.FILE_PARSE_FAIL.getCode(), e.getLocalizedMessage());
            }
        }
        return true;
    }

    /**
     * 处理解析路径中含段的逻辑
     *
     * @param filePath 目录路径
     * @param dataFile 系统指定的数据文件存放目录
     */
    private String processHasSectionDir(String filePath, File dataFile) {
        String section = "";
        File secFile;
        Path source = Paths.get(filePath);
        //如果文件路径是以段结尾的，那么获取路径的最后一部分作为段名
        if (filePath.endsWith("段")) {
            String[] dirSplit = filePath.split("/");
            section = dirSplit[dirSplit.length - 1];
            secFile = new File(inspectDataPath);
        } else {
            //否则从路径中提取段名
            String[] pathParts = filePath.split("/");
            for (String part : pathParts) {
                if (part.contains("段")) {
                    section = part;
                    break;
                }
            }
            String childPath = File.separator + section;
            secFile = new File(inspectDataPath + childPath);
        }
        Path destPath = Paths.get(secFile.getAbsolutePath() + filePath.substring(filePath.lastIndexOf("/")));
        try {
            DeleteCurrentDirectoryUtil.deleteCurrentDirectoryWithWalkFileTree(destPath);
        } catch (Exception e) {
            log.error("删除已有文件报错:{}", e.getLocalizedMessage());
        }
        if (!secFile.exists()) {
            secFile.mkdirs();
        }
        Path destination = Paths.get(secFile.getAbsolutePath());
        try {
            processMap.put(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId() + "SOURCE", filePath);
            processMap.put(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId() + "DEST", secFile.getAbsolutePath() + filePath.substring(filePath.lastIndexOf("/")));
            FileUtil.copyDirectory(source, destination);
            processMap.remove(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId() + "SOURCE");
            processMap.remove(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId() + "DEST");
        } catch (IOException e) {
            log.error("文件夹复制错误:{}", e.getLocalizedMessage());
        }
        return section;
    }

    /**
     * 获取所有包含文件的目录
     */
    private List<File> getDirectoriesWithFiles(File rootFile) {
        List<File> result = new ArrayList<>();
        File[] files = rootFile.listFiles();
        if (ArrayUtil.isEmpty(files)) {
            return result;
        }

        // 如果根目录直接包含文件，则直接返回
        if (files[0].isFile()) {
            boolean hasImage = false;
            for (File file : files) {
                if (FileUtil.judgeImageFileOrXml(file)) {
                    hasImage = true;
                    break;
                }
            }
            if (hasImage) {
                result.add(rootFile);
                return result;
            }
        }

        // 否则递归查找包含文件的子目录
        collectDirectoriesWithFiles(rootFile, result);
        return result;
    }

    /**
     * 递归收集包含文件的目录
     */
    private void collectDirectoriesWithFiles(File dir, List<File> result) {
        File[] files = dir.listFiles();
        if (ArrayUtil.isEmpty(files)) {
            return;
        }

        // 检查是否包含文件
        boolean hasFile = Arrays.stream(files).anyMatch(FileUtil::judgeImageFileOrXml);
        if (hasFile) {
            result.add(dir);
        } else {
            // 继续递归子目录
            for (File subDir : files) {
                if (subDir.isDirectory()) {
                    collectDirectoriesWithFiles(subDir, result);
                }
            }
        }
    }

    /**
     * 处理一批目录
     */
    public void processFileFolderParse(Long taskId, File file, AtomicInteger processedDirs, AtomicInteger totalDirs) throws ServiceException {
        try {
            processParsedDataOptimized(taskId, file);

            // 更新进度
            int current = processedDirs.incrementAndGet();
            //更新导入文件数
            taskMapper.updateImportNumsByTaskId(taskId, current);
            int total = totalDirs.get();
//            double progress = ((double) current / total) * 100;

        } catch (Exception e) {
            log.error("处理目录 {} 出错: {}", file.getAbsolutePath(), e.getMessage(), e);
            throw new ServiceException(ResultCode.FILE_PARSE_FAIL.getCode(), e.getLocalizedMessage());
        }
    }

    /**
     * 处理一批目录
     */
    public void processFileFolderParseNoTask(File file, AtomicInteger processedDirs, AtomicInteger totalDirs) throws ServiceException {
        try {
            processParsedDataOptimized(null, file);

            // 更新进度
            int current = processedDirs.incrementAndGet();

        } catch (Exception e) {
            log.error("处理目录 {} 出错: {}", file.getAbsolutePath(), e.getMessage(), e);
            throw new ServiceException(ResultCode.FILE_PARSE_FAIL.getCode(), e.getLocalizedMessage());
        }
    }

    /**
     * 处理一批目录
     */
    public void processBatch(List<File> batch, Long taskId, AtomicInteger processedDirs, AtomicInteger totalDirs) {
        for (File file : batch) {
            try {
                processParsedDataOptimized(taskId, file);

                // 更新进度
                int current = processedDirs.incrementAndGet();
                int total = totalDirs.get();
                double progress = ((double) current / total) * 100;
                processMap.put(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId(), "分析进度：" +
                        String.format("%.2f%%", progress));

            } catch (Exception e) {
                log.error("处理目录 {} 出错: {}", file.getAbsolutePath(), e.getMessage(), e);
            }
        }
    }

    /**
     * 优化的数据解析处理方法
     */
    public void processParsedDataOptimized(Long taskId, File file) throws ServiceException {
        File[] xmlFiles = file.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));
        if (xmlFiles != null && xmlFiles.length > 0) {
            try {
                XmlDataParseBO xmlDataParseBO = XmlParserDOM.parseXMLFile(xmlFiles[0]);
                if (null == xmlDataParseBO) {
                    xmlDataParseBO = XmlParserDOM.parseDataByDirName(file.getName());
                    if (null == xmlDataParseBO) {
                        return;
                    }
                    if (file.getAbsolutePath().contains("段")) {
                        String[] pathParts = file.getAbsolutePath().split("/");
                        for (String part : pathParts) {
                            if (part.endsWith("段")) {
                                xmlDataParseBO.setSectionName(part);
                                break;
                            }
                        }
                    } else {
                        xmlDataParseBO.setSectionName("未知段");
                    }
                }
                detailParseDataOptimized(taskId, file, xmlDataParseBO);
            } catch (Exception e) {
                log.error("解析出错:{}", e.getLocalizedMessage());
                throw new ServiceException(ResultCode.FILE_PARSE_FAIL);
            }
        } else {
            log.info("{}目录下：XML文件不存在", file.getName());
            XmlDataParseBO xmlDataParseBO = XmlParserDOM.parseDataByDirName(file.getName());
            if (null == xmlDataParseBO) {
                return;
            }
            if (file.getAbsolutePath().contains("段")) {
                String[] pathParts = file.getAbsolutePath().split("/");
                for (String part : pathParts) {
                    if (part.endsWith("段")) {
                        xmlDataParseBO.setSectionName(part);
                        break;
                    }
                }
            } else {
                xmlDataParseBO.setSectionName("未知段");
            }
            detailParseDataOptimized(taskId, file, xmlDataParseBO);
        }
    }

    /**
     * 优化的数据明细解析
     */
    public void detailParseDataOptimized(Long taskId, File file, XmlDataParseBO xmlDataParseBO) throws ServiceException {
        // 获取或创建关联实体
        Long sectionId = getOrCreateSectionId(xmlDataParseBO);
        Long lineId = getOrCreateLineId(xmlDataParseBO, sectionId);
        Long stationId = getOrCreateStationId(xmlDataParseBO, lineId);
        Long deviceId = getOrCreateDeviceId(xmlDataParseBO, stationId);

        if (null == deviceId) {
            return;
        }

        // 创建巡查数据记录
        InspectionData inspectionData = createInspectionData(xmlDataParseBO, taskId, deviceId, sectionId, lineId, stationId, file);

        // 批量保存巡查数据
        super.save(inspectionData);
        // 处理图片有效性验证
//        processFolderValidation(file, inspectionData);
        //taskScheduler.submit(()->processFolderValidation(file, inspectionData));
    }

    /**
     * 获取或创建供电段ID
     */
    private Long getOrCreateSectionId(XmlDataParseBO xmlDataParseBO) {
        if (StrUtil.isBlank(xmlDataParseBO.getSectionName())) {
            xmlDataParseBO.setSectionName("未知段");
        }
        String sectionKey = xmlDataParseBO.getSectionName(); // 假设线路名用于确定供电段
        if (sectionCache.containsKey(sectionKey)) {
            return sectionCache.get(sectionKey);
        }

        String sectionName = getSectionNameFromPath(xmlDataParseBO);

        Section existingSection = sectionMapper.getSectionByName(sectionName);
        if (existingSection != null) {
            Long sectionId = existingSection.getId();
            sectionCache.put(sectionKey, sectionId);
            return sectionId;
        }

        // 创建新供电段
        Section newSection = new Section();
        newSection.setSectionCode(sectionName).setSectionName(sectionName);
        try {
            sectionMapper.insert(newSection);
            Long sectionId = newSection.getId();
            sectionCache.put(sectionKey, sectionId);
            return sectionId;
        } catch (Exception e) {
            log.error("重复写入供电段:{}", sectionName);
            // 重新查询
            Section foundSection = sectionMapper.getSectionByName(sectionName);
            if (foundSection != null) {
                Long sectionId = foundSection.getId();
                sectionCache.put(sectionKey, sectionId);
                return sectionId;
            }
        }
        return null;
    }

    /**
     * 获取或创建线路ID
     */
    private Long getOrCreateLineId(XmlDataParseBO xmlDataParseBO, Long sectionId) {
        String lineKey = sectionId + "_" + xmlDataParseBO.getLineName();
        if (null != lineKey && lineCache.containsKey(lineKey)) {
            return lineCache.get(lineKey);
        }

        Line existingLine = lineMapper.getLineBySectionIdAndName(sectionId, xmlDataParseBO.getLineName());
        if (existingLine != null) {
            Long lineId = existingLine.getId();
            lineCache.put(lineKey, lineId);
            return lineId;
        }

        // 创建新线路
        Line newLine = new Line();
        newLine.setLineCode(xmlDataParseBO.getLineName())
                .setLineName(xmlDataParseBO.getLineName())
                .setSectionId(sectionId).setDirection(xmlDataParseBO.getDirection());
        try {
            lineMapper.insert(newLine);
            Long lineId = newLine.getId();
            lineCache.put(lineKey, lineId);
            return lineId;
        } catch (Exception e) {
            log.error("重复写入线路:{}", xmlDataParseBO.getLineName());
            // 重新查询
            Line foundLine = lineMapper.getLineBySectionIdAndName(sectionId, xmlDataParseBO.getLineName());
            if (foundLine != null) {
                Long lineId = foundLine.getId();
                lineCache.put(lineKey, lineId);
                return lineId;
            }
        }

        return null;
    }

    /**
     * 获取或创建区站ID
     */
    private Long getOrCreateStationId(XmlDataParseBO xmlDataParseBO, Long lineId) {
        String stationKey = lineId + "_" + xmlDataParseBO.getStationName();
        if (stationCache.containsKey(stationKey)) {
            return stationCache.get(stationKey);
        }

        Station existingStation = stationMapper.getStationByLineIdAndName(lineId, xmlDataParseBO.getStationName());
        if (existingStation != null) {
            Long stationId = existingStation.getId();
            stationCache.put(stationKey, stationId);
            return stationId;
        }

        // 创建新区站
        Station newStation = new Station();
        newStation.setStationCode(xmlDataParseBO.getStationName())
                .setStationName(xmlDataParseBO.getStationName())
                .setLineId(lineId);
        try {
            stationMapper.insert(newStation);
            Long stationId = newStation.getId();
            stationCache.put(stationKey, stationId);
            return stationId;
        } catch (Exception e) {
            log.error("重复写入区站:{}", xmlDataParseBO.getStationName());
            // 重新查询
            Station foundStation = stationMapper.getStationByLineIdAndName(lineId, xmlDataParseBO.getStationName());
            if (foundStation != null) {
                Long stationId = foundStation.getId();
                stationCache.put(stationKey, stationId);
                return stationId;
            }
        }

        return null;
    }

    /**
     * 获取或创建设备ID
     */
    private Long getOrCreateDeviceId(XmlDataParseBO xmlDataParseBO, Long stationId) {
        String deviceKey = stationId + "_" + xmlDataParseBO.getPoleNo();
        if (deviceCache.containsKey(deviceKey)) {
            return deviceCache.get(deviceKey);
        }

        Device existingDevice = deviceMapper.getDeviceByCode(stationId, xmlDataParseBO.getPoleNo());
        if (existingDevice != null) {
            // 更新设备信息
            existingDevice.setDeviceCode(xmlDataParseBO.getPoleNo())
                    .setStationId(stationId)
                    .setPillarNo(xmlDataParseBO.getPoleNo())
                    .setFixLocation(xmlDataParseBO.getFixLocation())
                    .setInstallationDate(null == xmlDataParseBO.getInspectionTime() ? null : xmlDataParseBO.getInspectionTime().toLocalDate())
                    .setStatus(Device.DEVICE_STATUS_RUN);
            deviceMapper.updateById(existingDevice);

            Long deviceId = existingDevice.getId();
            deviceCache.put(deviceKey, deviceId);
            return deviceId;
        }

        // 创建新设备
        Device newDevice = new Device();
        newDevice.setDeviceCode(xmlDataParseBO.getPoleNo())
                .setStationId(stationId)
                .setPillarNo(xmlDataParseBO.getPoleNo())
                .setFixLocation(xmlDataParseBO.getFixLocation())
                .setInstallationDate(null == xmlDataParseBO.getInspectionTime() ? null : xmlDataParseBO.getInspectionTime().toLocalDate())
                .setStatus(Device.DEVICE_STATUS_RUN);
        try {
            deviceMapper.insert(newDevice);
            Long deviceId = newDevice.getId();
            deviceCache.put(deviceKey, deviceId);
            return deviceId;
        } catch (Exception e) {
            log.error("重复写入设备:{}", xmlDataParseBO.getPoleNo());
            // 重新查询
            Device foundDevice = deviceMapper.getDeviceByCode(stationId, xmlDataParseBO.getPoleNo());
            if (foundDevice != null) {
                Long deviceId = foundDevice.getId();
                deviceCache.put(deviceKey, deviceId);
                return deviceId;
            }
        }

        return null;
    }

    /**
     * 创建巡查数据对象
     */
    private InspectionData createInspectionData(XmlDataParseBO xmlDataParseBO, Long taskId, Long deviceId, Long sectionId,
                                                Long lineId, Long stationId, File file) {
        // 确保目标目录存在
        File inspectDataDir = new File(inspectDataPath);
        if (!inspectDataDir.exists()) {
            inspectDataDir.mkdirs();
        }

        InspectionData inspectionData = new InspectionData();
        inspectionData.setImageValid(0)
                .setTaskId(taskId)
                .setInspectionTime(xmlDataParseBO.getInspectionTime())
                .setDeviceId(deviceId)
                .setSectionId(sectionId)
                .setLineId(lineId)
                .setStationId(stationId)
                .setAnalysisStatus(InspectionData.ANALYSIS_STATUS_WAIT_A)
                .setKmSign(xmlDataParseBO.getKmSign())
                .setSpeed(xmlDataParseBO.getSpeed())
                .setPantographStatus(InspectionData.PANTOGRAPH_STATUS_RIGHT)
                .setLocomotiveNumber(xmlDataParseBO.getLocomotiveNumber());

        // 设置图片路径
        String relativePath = file.getAbsolutePath().replace(inspectDataPath, "");
        if (!relativePath.startsWith("/")) {
            relativePath = "/" + relativePath;
        }
        inspectionData.setLocomotiveImage(relativePath);
        return inspectionData;
    }

    /**
     * 处理图片有效性验证
     */
    @Override
    public void processImageValidation(File file, InspectionData inspectionData) {
        String absolutePath = file.getAbsolutePath();
        List<String> imagePathList = new ArrayList<>();
        FilteredFileLister.listFilepathByExtension(imagePathList, absolutePath);
        if (CollUtil.isEmpty(imagePathList)) {
            return;
        }

        // 批量处理图片验证
//        List<CompletableFuture<Void>> validationFutures = new ArrayList<>();

        for (String imagePath : imagePathList) {
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> validateSingleImage(imagePath, inspectionData), executorService);
//            validationFutures.add(future);
            validateSingleImage(imagePath, inspectionData);
        }

        // 等待所有验证完成
//        CompletableFuture.allOf(validationFutures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * 处理文件夹图片验证
     */
    @Override
    public void processFolderValidation(File file, InspectionData inspectionData) {
        try {
            List<String> imagePathList = new ArrayList<>();
            FilteredFileLister.listFilepathByExtension(imagePathList, file.getAbsolutePath());
            if (CollUtil.isEmpty(imagePathList)) {
                inspectionData.setAnalysisStatus(AnalyzeStatusEnum.WAIT_REVIEW.getCode());
                super.updateById(inspectionData);
                return;
            }
            inspectionData.setImageNum(imagePathList.size());

            String category = resolveAnalysisCategory(inspectionData);
            AiAnalysisFolderRespBO aiAnalysisFolderResp = analyzeFolderWithFallback(inspectionData, file.getAbsolutePath(), imagePathList, category);

            // 分析失败或结果为空：全部图片按"正常"登记为待审批
            if (null == aiAnalysisFolderResp || !aiAnalysisFolderResp.getSuccess()) {
                markImagesNormalAndPending(imagePathList, inspectionData);
                return;
            }
            List<AiAnalysisRespDataBO> results = aiAnalysisFolderResp.getResults();
            if (CollUtil.isEmpty(results)) {
                markImagesNormalAndPending(imagePathList, inspectionData);
                return;
            }
            applyAnalysisResults(results, inspectionData);

            // 仅异物异常时 foreignObjectProcess 不会置状态，正常图在 pantographStatus 已为异常时也会跳过赋值，
            // 因此成功路径统一在此置为待审批
            inspectionData.setAnalysisStatus(AnalyzeStatusEnum.WAIT_REVIEW.getCode());
            log.info("巡查数据状态已更新成：待审批");
            super.updateById(inspectionData);
        } catch (Exception e) {
            log.error("分析解析文件夹出错:{}", e.getLocalizedMessage(), e);
//            inspectionData.setAnalysisStatus(AnalyzeStatusEnum.WAIT_REVIEW.getCode());
            super.updateById(inspectionData);
        }
    }

    /**
     * 解析本次分析使用的 category：基于线路名+设备位置查配置，缺失时回退为 "e"
     */
    private String resolveAnalysisCategory(InspectionData inspectionData) {
        Device device = deviceMapper.selectById(inspectionData.getDeviceId());
        Line line = lineMapper.selectById(inspectionData.getLineId());
        if (null != device && null != line) {
            String category = SystemConstants.LINE_LOCATION_CONFIG.get(line.getLineName() + "-" + device.getFixLocation());
            if (StrUtil.isNotBlank(category)) {
                return category;
            }
        }
        return lineCategory;
    }

    /**
     * 先对主文件夹整体分析；失败则降级为逐个子文件夹分析，命中首个成功结果即返回
     */
    private AiAnalysisFolderRespBO analyzeFolderWithFallback(InspectionData inspectionData, String folderPath, List<String> imagePathList, String category) {
        AiAnalysisFolderRespBO resp = null;
        try {
            resp = invokeFolderAnalysis(folderPath, category);
        } catch (ServiceException e) {
            log.error("算法分析主目录出错:{}", e.getLocalizedMessage());
        }
        if (null != resp) {
            return resp;
        }
        log.info("主文件夹分析失败，开始尝试子文件夹分析，共{}个子文件夹", imagePathList.size());
        Set<String> subFolderSet = new LinkedHashSet<>();
        for (String imagePath : imagePathList) {
            int slashIdx = imagePath.lastIndexOf("/");
            if (slashIdx > 0) {
                subFolderSet.add(imagePath.substring(0, slashIdx));
            }
        }
        //判断子文件夹是否为空，不为空，直接返回
        if (CollUtil.isEmpty(subFolderSet)) {
            if(null==inspectionData.getTaskId()){
                inspectionData.setAnalysisStatus(AnalyzeStatusEnum.WAIT_ANALYZE.getCode());
                super.baseMapper.updateById(inspectionData);
                return null;
            }else{
                taskService.pauseAnalysis(inspectionData.getTaskId());
                inspectionData.setAnalysisStatus(AnalyzeStatusEnum.WAIT_ANALYZE.getCode());
                super.baseMapper.updateById(inspectionData);
                Map<String, Object> taskStatusMap = new HashMap<>();
                taskStatusMap.put("taskId", inspectionData.getTaskId());
                taskStatusMap.put("status", "PAUSE");
                WebSocketSessionManager.broadcastMsg(WebSocketMsg.msg("TASK_CHECK_PAUSE", apiUtils.currentUserId() + "", taskStatusMap));
            }
            return null;
        }
        //分析子图片文件夹
        for (String subFolder : subFolderSet) {
            AiAnalysisFolderRespBO subResp = null;
            try {
                subResp = invokeFolderAnalysis(subFolder, category);
            } catch (ServiceException e) {
                log.error("子文件夹分析出错:{}", e.getLocalizedMessage(), e);
                inspectionData.setAnalysisStatus(AnalyzeStatusEnum.WAIT_ANALYZE.getCode());
                super.baseMapper.updateById(inspectionData);
                taskService.pauseAnalysis(inspectionData.getTaskId());
                Map<String, Object> taskStatusMap = new HashMap<>();
                taskStatusMap.put("taskId", inspectionData.getTaskId());
                taskStatusMap.put("status", "PAUSE");
                WebSocketSessionManager.broadcastMsg(WebSocketMsg.msg("TASK_CHECK_PAUSE", apiUtils.currentUserId() + "", taskStatusMap));
                throw new RuntimeException(e);
            }
            if (null != subResp && Boolean.TRUE.equals(subResp.getSuccess())) {
                log.info("子文件夹分析成功: path={}", subFolder);
                return subResp;
            }
        }
        return null;
    }

    /**
     * 调用 AI 文件夹分析接口，异常或返回 null 时记日志并返回 null
     */
    private AiAnalysisFolderRespBO invokeFolderAnalysis(String folderPath, String category) throws ServiceException {
        AiRequest2BO req = new AiRequest2BO();
        req.setFolder_path(folderPath).setConfig_type(category).set_delete(false);
        try {
            long t1 = System.currentTimeMillis();
            log.info("ai_analysisImage_req:请求参数:{}", req);
            AiAnalysisFolderRespBO resp = aiClient.analysis2(req);
            log.info("ai_analysis2_请求耗时:{}ms, 结果={}", System.currentTimeMillis() - t1, resp);
            if (null == resp) {
                log.error("aiClient.analysis2() 返回null，请检查Forest配置/AI服务/网络连接");
            }
            return resp;
        } catch (Exception e) {
            log.error("folderAnalysis_error: 路径={}, 错误类型={}, 错误信息={}",
                    folderPath, e.getClass().getName(), e.getMessage(), e);
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "算法分析出错");
        }
    }

    /**
     * 将全部图片按"正常"登记为 InspectionReviewResult，并把巡检数据置为待审批
     */
    private void markImagesNormalAndPending(List<String> imagePathList, InspectionData inspectionData) {
        for (String imagePath : imagePathList) {
            upsertNormalReviewResult(inspectionData, imagePath);
        }
        inspectionData.setPantographStatus(PantographStatusEnum.NORMAL.getCode());
        inspectionData.setAnalysisStatus(AnalyzeStatusEnum.WAIT_ANALYZE.getCode());
        log.info("巡查数据状态已更新成：待审批");
        super.updateById(inspectionData);
    }

    /**
     * 根据 AI 返回结果逐条处理：检出异常的走弯曲/异物处理并累加任务告警数；
     * 未检出异常的（含 status=正常 且 has_sdg=false 这种 anomaly_details 非空但内层为 null 的情况）登记为正常记录。
     */
    private void applyAnalysisResults(List<AiAnalysisRespDataBO> results, InspectionData inspectionData) {
        boolean locomotiveNumberCaptured = false;
        for (AiAnalysisRespDataBO data : results) {
            if (null == data) {
                continue;
            }
            if (!locomotiveNumberCaptured && CharSequenceUtil.isNotBlank(data.getLicense_plate()) && !"无".equals(data.getLicense_plate())) {
                inspectionData.setLocomotiveNumber(data.getLicense_plate());
                locomotiveNumberCaptured = true;
            }
            // AI 未找到图片文件时会返回全 null 的占位结果项（total=0），无 image_path，跳过
            if (CharSequenceUtil.isBlank(data.getImage_path())) {
                continue;
            }
            if (hasDetectedAnomaly(data)) {
                processAnomaly(data, inspectionData);
                incrementTaskAiAlarms(inspectionData.getTaskId());
                //分析发现异常，向库尔勒桌面程序发送新消息
                try {
                    AiAlarmBO aiAlarmBO = new AiAlarmBO();
                    aiAlarmBO.setTitle("新异常数据到达")
                            .setContent("有新的异常数据到达，请及时处理")
                            .setUrl(fileSyncHost + outAccessToken);
                    aiAlarmClient.aiAlarm(aiAlarmBO);
                    log.info("向桌面程序发送异常报警消息成功:{}", fileSyncHost + outAccessToken);
                } catch (Exception e) {
                    log.error("向桌面程序发送消息出错:{}", fileSyncHost + outAccessToken, e);
                }
            } else {
                upsertNormalReviewResult(inspectionData, data.getImage_path());
                if (null == inspectionData.getPantographStatus() || PantographStatusEnum.UNKNOWN.getCode().equals(inspectionData.getPantographStatus())) {
                    inspectionData.setPantographStatus(PantographStatusEnum.NORMAL.getCode());
                    inspectionData.setAnalysisStatus(AnalyzeStatusEnum.WAIT_REVIEW.getCode());
                }
            }
        }
    }

    /**
     * 是否检出实际异常：bending 或 foreign_object 任一 detected=true。
     * anomaly_details 为 null、或内层对象为 null、或 detected 全为 false 均视为未检出。
     */
    private boolean hasDetectedAnomaly(AiAnalysisRespDataBO data) {
        AiAnalysisAnomalyDetailBO details = data.getAnomaly_details();
        if (null == details) {
            return false;
        }
        boolean bendingDetected = null != details.getBending() && Boolean.TRUE.equals(details.getBending().getDetected());
        boolean foreignDetected = null != details.getForeign_object() && Boolean.TRUE.equals(details.getForeign_object().getDetected());
        return bendingDetected || foreignDetected;
    }

    /**
     * 处理异常分支：弯曲或异物
     * 弯曲/异物各自按 detected 标志独立判定，避免一方对象非空但未检出时跳过另一方
     */
    private void processAnomaly(AiAnalysisRespDataBO data, InspectionData inspectionData) {
        AiAnalysisAnomalyDetailBO details = data.getAnomaly_details();
        boolean bendingDetected = null != details.getBending() && Boolean.TRUE.equals(details.getBending().getDetected());
        boolean foreignDetected = null != details.getForeign_object() && Boolean.TRUE.equals(details.getForeign_object().getDetected());
        if (bendingDetected) {
            bendingProcess(data, inspectionData, data.getImage_path());
        }
        if (foreignDetected) {
            foreignObjectProcess(data, inspectionData, data.getImage_path());
        }
    }

    /**
     * 任务 AI 报警数 +1
     */
    private void incrementTaskAiAlarms(Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (null == task) {
            return;
        }
        task.setAiAlarms(null == task.getAiAlarms() ? 1 : task.getAiAlarms() + 1);
        taskMapper.updateById(task);
    }

    /**
     * 写入或更新一条"正常"状态的 InspectionReviewResult
     */
    private void upsertNormalReviewResult(InspectionData inspectionData, String imagePath) {
        String relativePath = imagePath.replace(inspectDataPath, "");
        // 原子写入：依赖 (inspection_id, image_path) 唯一索引 ON DUPLICATE KEY UPDATE，
        // 避免多线程并发处理同一巡检图片时先查后写造成的锁等待与 ob_query_timeout 超时
        InspectionReviewResult reviewResult = new InspectionReviewResult();
        reviewResult.setTaskId(inspectionData.getTaskId())
                .setPantographStatus(PantographStatusEnum.NORMAL.getCode())
                .setInspectionId(inspectionData.getId())
                .setLocomotiveNumber(inspectionData.getLocomotiveNumber())
                .setImageValid(1)
                .setImagePath(relativePath)
                .setNetPath(imagePath.replace(inspectDataPath, inspectHost));
        if (null == inspectionData.getTaskId()) {
            inspectionReviewResultMapper.upsertNormalResultNoTask(reviewResult);
        } else {
            inspectionReviewResultMapper.upsertNormalResult(reviewResult);
        }
    }

    /**
     * 验证单张图片
     */
    private void validateSingleImage(String imagePath, InspectionData inspectionData) {
        AiRequestBO aiRequestBO = new AiRequestBO();
        aiRequestBO.setImage(imagePath);
        try {
            long t1 = System.currentTimeMillis();
            ImageValidRespBO imageValidResp = aiClient.imageValid(aiRequestBO);
            log.info("imageValid_请求耗时:{}", System.currentTimeMillis() - t1);
            log.info("图片有效性验证结果:请求参数:{}，响应结果:{}", aiRequestBO, imageValidResp);

            if (null != imageValidResp && imageValidResp.getData() == 1) {
                // 图片有效，保存审核结果
                String relativeImagePath = imagePath.replace(inspectDataPath, "");
                InspectionReviewResult inspectionReviewResult =
                        inspectionReviewResultMapper.getEntityByInspectionId(inspectionData.getId(), relativeImagePath);

                if (null == inspectionReviewResult) {
                    inspectionReviewResult = new InspectionReviewResult();
                }

                inspectionReviewResult.setInspectionId(inspectionData.getId())
                        .setImagePath(relativeImagePath)
                        .setImageValid(1)
                        .setLocomotiveNumber(inspectionData.getLocomotiveNumber())
                        .setPantographStatus(InspectionData.PANTOGRAPH_STATUS_RIGHT);

                if (null == inspectionReviewResult.getId()) {
                    inspectionReviewResultMapper.insert(inspectionReviewResult);
                } else {
                    inspectionReviewResultMapper.updateById(inspectionReviewResult);
                }
            } else {
                // 图片无效，删除文件
                new File(imagePath).delete();
            }
            //ai分析图片
            aiCheckData(imagePath, inspectionData);
        } catch (Exception e) {
            log.error("imageValid_error:{}", e.getLocalizedMessage());
        }
    }

    /**
     * 验证文件夹
     */
    private void validateFolder(String folderPath) {
        AiRequest2BO aiRequestBO = new AiRequest2BO();
        aiRequestBO.setFolder_path(folderPath);
        try {
            long t1 = System.currentTimeMillis();
            aiClient.imageValid2(aiRequestBO);
            log.info("imageValid_请求耗时:{}", System.currentTimeMillis() - t1);
        } catch (Exception e) {
            log.error("validateFolder:{}", e.getLocalizedMessage());
        }
    }

    /**
     * 获取供电段名称
     */
    private String getSectionNameFromPath(XmlDataParseBO xmlDataParseBO) {
        // 这里可以根据实际路径结构提取供电段名称
        // 示例：从线路名或其他信息推断
        return xmlDataParseBO.getSectionName(); // 简化实现，实际可能需要更复杂的逻辑
    }

    /**
     * 清空缓存
     */
    private void clearCache() {
        sectionCache.clear();
        lineCache.clear();
        stationCache.clear();
        deviceCache.clear();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteById(Long id) {
        InspectionData inspectionData = super.getById(id);
        // 2. 【新增】判空保护
        if (inspectionData == null) {
            // 可以选择返回 false，或者抛出自定义异常
            return false;
        }
        inspectionReviewResultMapper.deleteByInspectionId(id);
        super.removeById(inspectionData);
        if(null != inspectionData.getTaskId()){
            long taskFolderCount = super.baseMapper.countByTaskId(inspectionData.getTaskId());
            if (taskFolderCount == 0) {
                //判断删除的是不是任务的最后一个文件夹，是删除任务
                taskMapper.deleteById(inspectionData.getTaskId());
            }
        }
        return true;
    }

    @Override
    public InspectionData getReviewData(Long inspectionId) {
        InspectionData inspectionData = super.getById(inspectionId);
        Section section = sectionMapper.selectById(inspectionData.getSectionId());
        inspectionData.setSectionName(null == section ? "" : section.getSectionName());
        Line line = lineMapper.selectById(inspectionData.getLineId());
        inspectionData.setLineName(null == line ? "" : line.getLineName());
        Station station = stationMapper.selectById(inspectionData.getStationId());
        inspectionData.setStationName(null == station ? "" : station.getStationName());
        Device device = deviceService.getById(inspectionData.getDeviceId());
        if (null != device) {
            inspectionData.setDeviceCode(device.getDeviceCode()).setPillarNo(device.getPillarNo());
        }
        List<ImageReviewInfoVO> imageReviewList = new ArrayList<>();
        List<InspectionReviewResultVO> reviewResultVOList = inspectionReviewResultMapper.getDetailListByInspectionId(inspectionId);
        if (CollUtil.isEmpty(reviewResultVOList)) {
            inspectionData.setConfirmStatus(ConfirmStatusEnum.VIEWED.getCode());
            inspectionData.setAnalysisStatus(AnalyzeStatusEnum.REVIEWED.getCode());
            super.updateById(inspectionData);
            return inspectionData;
        }

        for (InspectionReviewResultVO reviewResultVO : reviewResultVOList) {
            String imagePath = reviewResultVO.getImagePath();
            if (StrUtil.isBlank(imagePath) || !new File(inspectDataPath, imagePath).isFile()) {
                continue;
            }
            ImageReviewInfoVO imageReviewInfoVO = new ImageReviewInfoVO();
            String sdgStatus = PantographStatusEnum.NORMAL.getCode().equals(reviewResultVO.getPantographStatus())?"正常":JSONUtil.toList(reviewResultVO.getMarkedRegions(), DefectMarkBO.class).stream().map(DefectMarkBO::getDefectType).distinct().collect(Collectors.joining("、"));
            imageReviewInfoVO.setImageId(reviewResultVO.getId()).setImageName(reviewResultVO.getNetPath())
                    .setAnalyzer(reviewResultVO.getCreatedUser()).setProblemDesc(reviewResultVO.getProblemDesc()).setMisreport(reviewResultVO.getMisreport()).setConfirmStatus(reviewResultVO.getConfirmStatus() == null ? ConfirmStatusEnum.NO_VIEW.getCode() : reviewResultVO.getConfirmStatus())
                    .setPantographStatus(sdgStatus).setImageValid(reviewResultVO.getImageValid()).setReliability(reviewResultVO.getReliability())
                    .setMarkedRegions(StringUtils.isNotBlank(reviewResultVO.getMarkedRegions()) ? JSONUtil.toList(reviewResultVO.getMarkedRegions(), DefectMarkBO.class) : new ArrayList<>());
            imageReviewList.add(imageReviewInfoVO);
        }
        inspectionData.setImageFileList(imageReviewList);
        return inspectionData;
    }

    @Override
    public Boolean validImage(String imagePath) {
        AiRequestBO aiRequestBO = new AiRequestBO();
//        String imageName = FilteredFileLister.getFirstFileByExtension(inspectDataPath + imagePath);
        aiRequestBO.setImage(imagePath);
//        aiRequestBO.setImage(inspectDataPath + imagePath + imageName);
        ImageValidRespBO jsonObject = aiClient.imageValid(aiRequestBO);
        log.info("validImage:{}", jsonObject);
        return true;
    }

    @Override
    public Boolean startAnalysis(List<Long> idList) {
        List<InspectionData> inspectionDataList = super.listByIds(idList);
        if (CollUtil.isEmpty(inspectionDataList)) {
            return false;
        }
        inspectionDataList = inspectionDataList.stream().filter(id -> AnalyzeStatusEnum.WAIT_ANALYZE.getCode().equals(id.getAnalysisStatus())).collect(Collectors.toList());
        //AI检测数据
        aiCheckData(inspectionDataList);
        return true;
    }

    /**
     * @param inspectionDataList 巡查数据列表
     * @desc AI分析数据
     */
    private void aiCheckData(List<InspectionData> inspectionDataList) {
        processMap.put(PROCESS_CONSTANT_ANALYSIS + apiUtils.currentUserId(), "0%");
        int inspectionCount = 0;
        for (InspectionData inspectionData : inspectionDataList) {
            inspectionData.setAnalysisStatus(AnalyzeStatusEnum.WAIT_REVIEW.getCode());
            log.info("巡查数据状态已更新成：待审批,{}", inspectionData);
            super.updateById(inspectionData);
            inspectionCount++;
            processMap.put(PROCESS_CONSTANT_ANALYSIS + apiUtils.currentUserId(), NumberUtil.getTwoNumberDivideToPercent(inspectionCount, inspectionDataList.size()));
        }
        processMap.put(PROCESS_CONSTANT_ANALYSIS + apiUtils.currentUserId(), "100%");
    }

    /**
     * @param imagePath      图片路径
     * @param inspectionData 巡查数据
     * @desc AI分析数据
     */
    private void aiCheckData(String imagePath, InspectionData inspectionData) {
        AiRequestBO aiRequestBO = new AiRequestBO();
        aiRequestBO.setImage(imagePath);
        //AI分析图片
        aiAnalysisImage(aiRequestBO, inspectionData, imagePath);
        inspectionData.setAnalysisStatus(AnalyzeStatusEnum.WAIT_REVIEW.getCode());
        log.info("巡查数据状态已更新成：待审批");
        super.updateById(inspectionData);
    }

    /**
     * AI分析图片
     *
     * @param aiRequestBO
     * @param inspectionData
     * @param imagePath
     */
    void aiAnalysisImage(AiRequestBO aiRequestBO, InspectionData inspectionData, String imagePath) {
        try {
            long t1 = System.currentTimeMillis();
            log.info("ai_analysisImage_result:请求参数:{}", JSONUtil.toJsonStr(aiRequestBO));
            AiAnalysisRespBO aiAnalysisRespBO = aiClient.analysis(aiRequestBO);
            log.info("aiCheckData_请求耗时:{}", System.currentTimeMillis() - t1);
            log.info("ai_analysisImage_result:响应结果:{}", JSONUtil.toJsonStr(aiAnalysisRespBO));
            if (CharSequenceUtil.isNotBlank(aiAnalysisRespBO.getData().getLicense_plate())) {
                inspectionData.setLocomotiveNumber(aiAnalysisRespBO.getData().getLicense_plate());
            }
            if (Boolean.TRUE.equals(aiAnalysisRespBO.getSuccess())) {
                if (Boolean.TRUE.equals(aiAnalysisRespBO.getData().getHas_sdg()) && "正常".equals(aiAnalysisRespBO.getData().getStatus())) {
                    if (null != inspectionData.getPantographStatus()) {
                        return;
                    }
                    inspectionData.setPantographStatus(PantographStatusEnum.NORMAL.getCode());
                } else {
                    //弯曲判断
                    if (null != aiAnalysisRespBO.getData().getAnomaly_details().getBending()) {
                        //bendingProcess(aiAnalysisRespBO, inspectionData, imagePath);
                        //异物判断
                    } else if (null != aiAnalysisRespBO.getData().getAnomaly_details().getForeign_object()) {
                        //foreignObjectProcess(aiAnalysisRespBO, inspectionData, imagePath);
                    }
                }
            }
        } catch (Exception e) {
            log.error("startAnalysis_error:{}", e.getLocalizedMessage());
        }
    }

    /**
     * 弯曲异常处理
     * <p>
     * markedRegions 写入结构为：[{"coord":"1,1,1,1","defectType":"弯曲"}]
     */
    void bendingProcess(AiAnalysisRespDataBO aiAnalysisRespData, InspectionData inspectionData, String imagePath) {
        AiAnalysisAnomalyDetailBO.Bending bending = aiAnalysisRespData.getAnomaly_details().getBending();
        if (Boolean.TRUE.equals(bending.getDetected())) {
            inspectionData.setPantographStatus(PantographStatusEnum.ERROR.getCode());
            List<List<Object>> bboxes = bending.getBboxes();
            InspectionReviewResult inspectionReviewResult = inspectionReviewResultMapper.getEntityByInspectionId(inspectionData.getId(), imagePath.replace(inspectDataPath, ""));
            if (null == inspectionReviewResult) {
                inspectionReviewResult = new InspectionReviewResult();
                inspectionReviewResult.setTaskId(inspectionData.getTaskId()).setPantographStatus(inspectionData.getPantographStatus()).setInspectionId(inspectionData.getId()).setProblemDesc("弯曲")
                        .setLocomotiveNumber(inspectionData.getLocomotiveNumber()).setImageValid(1)
                        .setImagePath(imagePath.replace(inspectDataPath, "")).setNetPath(aiAnalysisRespData.getImage_path().replace(inspectDataPath, inspectHost));
                ;
                List<DefectMarkBO> markedRegions = new ArrayList<>();
                for (List<Object> mark : bboxes) {
                    inspectionReviewResult.setReliability(mark.get(mark.size() - 1).toString());
                    markedRegions.add(buildDefectMark(joinCoord(mark.subList(0, mark.size() - 1)), "弯曲", DefectSourceTypeEnum.AI.getCode()));
                }
                inspectionReviewResult.setMarkedRegions(JSONUtil.toJsonStr(markedRegions));
                log.info("新增弯曲:{}", inspectionReviewResult);
                inspectionReviewResultMapper.insert(inspectionReviewResult);
                inspectionData.setAnalysisStatus(AnalyzeStatusEnum.WAIT_REVIEW.getCode());
            } else {
                inspectionReviewResult.setTaskId(inspectionData.getTaskId()).setPantographStatus(inspectionData.getPantographStatus())
                        .setLocomotiveNumber(inspectionData.getLocomotiveNumber()).setImageValid(1);
                List<DefectMarkBO> markedRegions = new ArrayList<>();
                for (List<Object> mark : bboxes) {
                    inspectionReviewResult.setReliability(mark.get(mark.size() - 1).toString());
                    markedRegions.add(buildDefectMark(joinCoord(mark.subList(0, mark.size() - 1)), "弯曲", DefectSourceTypeEnum.AI.getCode()));
                }
                if (CharSequenceUtil.isNotBlank(inspectionReviewResult.getMarkedRegions())) {
                    markedRegions.addAll(JSONUtil.toList(inspectionReviewResult.getMarkedRegions(), DefectMarkBO.class));
                }
                inspectionReviewResult.setMarkedRegions(JSONUtil.toJsonStr(markedRegions));
                log.info("更新弯曲:{}", inspectionReviewResult);
                inspectionReviewResultMapper.updateById(inspectionReviewResult);
                inspectionData.setAnalysisStatus(AnalyzeStatusEnum.WAIT_REVIEW.getCode());
            }
        }
    }

    /**
     * 将 bbox 坐标列表拼接为 "x1,y1,x2,y2" 形式
     */
    private String joinCoord(List<Object> coordItems) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coordItems.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(coordItems.get(i));
        }
        return sb.toString();
    }

    /**
     * 构造 markedRegions 单个元素：{"coord":"1,1,1,1","defectType":"弯曲"}
     */
    private DefectMarkBO buildDefectMark(String coord, String defectType, Integer sourceType) {
        return new DefectMarkBO().setCoord(coord).setDefectType(defectType).setSource(sourceType);
    }

    /**
     * 异物异常处理
     * <p>
     * markedRegions 写入结构为：[{"coord":"1,1,1,1","defectType":"异物"}]
     */
    @Override
    public void foreignObjectProcess(AiAnalysisRespDataBO aiAnalysisRespData, InspectionData inspectionData, String imagePath) {
        AiAnalysisAnomalyDetailBO.ForeignObject foreignObject = aiAnalysisRespData.getAnomaly_details().getForeign_object();
        if (foreignObject.getDetected()) {
            List<List<Object>> bboxes = foreignObject.getBboxes();
            inspectionData.setPantographStatus(PantographStatusEnum.ERROR.getCode());
            InspectionReviewResult inspectionReviewResult = inspectionReviewResultMapper.getEntityByInspectionId(inspectionData.getId(), imagePath.replace(inspectDataPath, ""));
            if (null == inspectionReviewResult) {
                inspectionReviewResult = new InspectionReviewResult();
                inspectionReviewResult.setTaskId(inspectionData.getTaskId()).setPantographStatus(inspectionData.getPantographStatus()).setInspectionId(inspectionData.getId()).setProblemDesc("异物")
                        .setLocomotiveNumber(inspectionData.getLocomotiveNumber()).setImageValid(1).setImagePath(imagePath.replace(inspectDataPath, ""))
                        .setNetPath(aiAnalysisRespData.getImage_path().replace(inspectDataPath, inspectHost));
                ;
                List<DefectMarkBO> markedRegions = new ArrayList<>();
                for (List<Object> mark : bboxes) {
                    inspectionReviewResult.setReliability(mark.get(mark.size() - 1).toString());
                    markedRegions.add(buildDefectMark(joinCoord(mark.subList(0, mark.size() - 1)), "异物", DefectSourceTypeEnum.AI.getCode()));
                }
                inspectionReviewResult.setMarkedRegions(JSONUtil.toJsonStr(markedRegions));
                log.info("新增异物:{}", inspectionReviewResult);
                inspectionReviewResultMapper.insert(inspectionReviewResult);
            } else {
                inspectionReviewResult.setTaskId(inspectionData.getTaskId()).setPantographStatus(inspectionData.getPantographStatus())
                        .setProblemDesc(CharSequenceUtil.isNotBlank(inspectionReviewResult.getProblemDesc()) ? inspectionReviewResult.getProblemDesc() + ",异物" : "异物")
                        .setLocomotiveNumber(inspectionData.getLocomotiveNumber()).setImageValid(1);
                List<DefectMarkBO> markedRegions = new ArrayList<>();
                for (List<Object> mark : bboxes) {
                    inspectionReviewResult.setReliability(mark.get(mark.size() - 1).toString());
                    markedRegions.add(buildDefectMark(joinCoord(mark.subList(0, mark.size() - 1)), "异物", DefectSourceTypeEnum.AI.getCode()));
                }
                if (CharSequenceUtil.isNotBlank(inspectionReviewResult.getMarkedRegions())) {
                    markedRegions.addAll(JSONUtil.toList(inspectionReviewResult.getMarkedRegions(), DefectMarkBO.class));
                }
                inspectionReviewResult.setMarkedRegions(JSONUtil.toJsonStr(markedRegions));
                log.info("更新异物:{}", inspectionReviewResult);
                inspectionReviewResultMapper.updateById(inspectionReviewResult);
            }
        }
    }

    @Override
    public String getParseProcess() {
        String source = processMap.get(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId() + "SOURCE");
        String destination = processMap.get(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId() + "DEST");
        if (CharSequenceUtil.isNotBlank(source) && CharSequenceUtil.isNotBlank(destination)) {
            try {
                long sourceSize;
                if (null == processMap.get(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId() + "SOURCE_SIZE")) {
                    sourceSize = ParallelDirectorySizeCalculator.getDirectorySizeParallel(Paths.get(source));
                    processMap.put(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId() + "SOURCE_SIZE", sourceSize + "");
                }
                sourceSize = Long.parseLong(processMap.get(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId() + "SOURCE_SIZE"));
                long destSize = ParallelDirectorySizeCalculator.getDirectorySizeParallel(Paths.get(destination));
                log.info("getParseProcess_copy_path:sourcepath,destpath:{},{}", source, destination);
                log.info("getParseProcess_copy_progress:sourcesize,destsize:{},{}", sourceSize, destSize);
                // 更新进度
                double progress = ((double) destSize / sourceSize) * 100;
                String copyProgress = String.format("%.2f%%", progress);
                if ("100.00%".equals(copyProgress)) {
                    processMap.remove(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId() + "SOURCE");
                    processMap.remove(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId() + "DEST");
                    processMap.remove(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId() + "SOURCE_SIZE");
                }
                return "拷贝进度：" + copyProgress;
            } catch (Exception e) {
                log.error("获取拷贝进度出错:{}", e.getLocalizedMessage());
                processMap.remove(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId() + "SOURCE_SIZE");
            }
        }
//        String folderPath = processMap.get(PROCESS_IMAGE_VALID + apiUtils.currentUserId());
//        if(CharSequenceUtil.isNotBlank(folderPath)){
//            AiRequest2BO aiRequest = new AiRequest2BO();
//            aiRequest.setFolder_path(folderPath);
//            ImageValidateProgressBO imageValidateProgressBO = aiClient.imageValidProgress(aiRequest);
//            double percent = null==imageValidateProgressBO?0:null==imageValidateProgressBO.getPercent()?0:imageValidateProgressBO.getPercent().doubleValue();
//            if(percent>=100){
//                processMap.remove(PROCESS_IMAGE_VALID + apiUtils.currentUserId());
//            }
//            return percent + "%";
//        }
        return processMap.get(PROCESS_CONSTANT_PARSE + apiUtils.currentUserId());
    }

    @Override
    public String getAnalysisProcess(Long inspectionId) {
        return processMap.get(PROCESS_CONSTANT_ANALYSIS + apiUtils.currentUserId());
    }

    @Override
    public Boolean analysisAll() {
        List<InspectionData> inspectionDataList = super.baseMapper.getInspectDataByAnalysisStatus(AnalyzeStatusEnum.WAIT_ANALYZE.getCode());
        aiCheckData(inspectionDataList);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteByIds(List<Long> ids) throws ServiceException {
        try {
            inspectionReviewResultMapper.deleteByInspectionIds(ids);
            for (Long id : ids) {
                //判断删除的是不是任务的最后一个文件夹，是删除任务
                InspectionData inspectionData = super.getById(id);
                if(null!=inspectionData.getTaskId()){
                    long taskFolderCount = super.baseMapper.countByTaskId(inspectionData.getTaskId());
                    if (taskFolderCount == 0) {
                        taskMapper.deleteById(inspectionData.getTaskId());
                    }
                }
            }
            super.removeByIds(ids);
        } catch (Exception e) {
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "删除失败");
        }
        return true;
    }

    @Override
    public List<List<Object>> getExportInspectionData(List<Long> ids, HttpServletResponse response) throws ServiceException {
        List<InspectionResultExportVO> inspectionResultExportList = inspectionReviewResultMapper.getInpectionReviewData(ids);
        List<List<Object>> baseDataList = new ArrayList<>();
        List<Object> title = Arrays.asList(
                "机车图片",
                "受电工状态",
                "检测时间",
                "段",
                "线路",
                "区站",
                "支柱号",
                "公里标",
                "分析人",
                "机车号",
                "图片有效",
                "问题描述"
        );
        baseDataList.add(title);
        if (CollUtil.isNotEmpty(inspectionResultExportList)) {
            for (InspectionResultExportVO result : inspectionResultExportList) {
                List<InspectionReviewResultVO> reviewResultVOList = inspectionReviewResultMapper.getImageVOByInspectionId(result.getInspectionId());
                if(CollUtil.isNotEmpty(reviewResultVOList)){
                    // 压缩包内图片名取机车图片路径的最后一段
                    for (InspectionReviewResultVO reviewResultVO : reviewResultVOList) {
                        Map<String,Object> param = new HashMap<>();
                        String imageInfo = null;
                        if (CharSequenceUtil.isNotBlank(reviewResultVO.getImagePath())) {
                            imageInfo = reviewResultVO.getNetPath();
                            param.put("imageUrl", imageInfo);
                            if (CharSequenceUtil.isNotBlank(reviewResultVO.getMarkedRegions()) && !"[]".equals(reviewResultVO.getMarkedRegions())) {
                                param.put("markedRegions", reviewResultVO.getMarkedRegions());
                            }
                        }
                        Integer pantographStatus = null == reviewResultVO.getPantographStatus()
                                ? result.getPantographStatus() : reviewResultVO.getPantographStatus();
                        List<Object> data = Lists.newArrayList(param, pantographStatusText(reviewResultVO.getMarkedRegions(), pantographStatus), result.getInspectionTime(), result.getSectionName(), result.getLineName(), result.getStationName(), result.getPillarNo(), result.getKmSign(), result.getAnalyzer(), result.getLocomotiveNumber(), LogicStatusEnum.getNameByCode(result.getImageValid()), reviewResultVO.getProblemDesc());
                        baseDataList.add(data);
                    }
                }

            }
        }
        return baseDataList;
    }
    @Override
    public void exportInspectionData(List<Long> ids, HttpServletResponse response) throws ServiceException {
        Long userId = apiUtils.currentUserId();
        AtomicInteger lastProgress = new AtomicInteger(-1);
        IntConsumer progressConsumer = progress -> ExportProgressUtil.send(userId,
                ExportProgressUtil.EXPORT_TYPE_INSPECTION_DATA, progress, lastProgress);
        progressConsumer.accept(0);
        List<String> title = Arrays.asList(
                "机车图片",
                "受电工状态",
                "检测时间",
                "段",
                "线路",
                "区站",
                "支柱号",
                "公里标",
                "分析人",
                "机车号",
                "图片有效",
                "问题描述"
        );
        List<InspectionResultExportVO> inspectionResultExportList = inspectionReviewResultMapper.getInpectionReviewData(ids);
        List<List<Object>> baseDataList = new ArrayList<>();
        List<String> imageBaseNameList = new ArrayList<>();
        if (CollUtil.isNotEmpty(inspectionResultExportList)) {
            for (InspectionResultExportVO result : inspectionResultExportList) {
                List<InspectionReviewResultVO> reviewResultVOList = inspectionReviewResultMapper.getImageVOByInspectionId(result.getInspectionId());
                if(CollUtil.isNotEmpty(reviewResultVOList)){
                    // 压缩包内图片名取机车图片路径的最后一段
                    String imageBaseName = lastPathSegment(result.getLocomotiveImage());
                    for (InspectionReviewResultVO reviewResultVO : reviewResultVOList) {
                        String imageInfo = null;
                        if (CharSequenceUtil.isNotBlank(reviewResultVO.getImagePath())) {
                            imageInfo = inspectDataPath+reviewResultVO.getImagePath();
                            if (CharSequenceUtil.isNotBlank(reviewResultVO.getMarkedRegions()) && !"[]".equals(reviewResultVO.getMarkedRegions())) {
                                imageInfo += ";" + reviewResultVO.getMarkedRegions();
                            }
                        }
                        Integer pantographStatus = null == reviewResultVO.getPantographStatus()
                                ? result.getPantographStatus() : reviewResultVO.getPantographStatus();
                        List<Object> data = Lists.newArrayList(imageInfo, pantographStatusText(reviewResultVO.getMarkedRegions(), pantographStatus), result.getInspectionTime(), result.getSectionName(), result.getLineName(), result.getStationName(), result.getPillarNo(), result.getKmSign(), result.getAnalyzer(), result.getLocomotiveNumber(), LogicStatusEnum.getNameByCode(result.getImageValid()), reviewResultVO.getProblemDesc());
                        baseDataList.add(data);
                        imageBaseNameList.add(imageBaseName);
                    }
                }

            }
        }
        //添加导出记录
        ExportInfo exportInfo = new ExportInfo();
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("ids", ids);
        exportInfo.setSourcePage("实时分析")
                .setExportType("实时分析")
                .setQueryParam(JSONUtil.toJsonStr(queryParam))
                .setFileName("实时分析"+System.currentTimeMillis()+".xlsx")
                .setQueryApi("/smart5cApi/inspectionData/getExportInspectionData")
                .setExportApi("/smart5cApi/inspectionData/exportInspectionData");
        try {
            ByteCountingServletResponse countingResponse = new ByteCountingServletResponse(response);
            excelProcessUtil.exportWithPicturesAsZipNamed(title, baseDataList, 0, imageBaseNameList,
                    "inspectDataReport.zip", countingResponse, progressConsumer);
            exportInfo.setQuantity(String.valueOf(baseDataList.size()))
                    .setFileSize(countingResponse.getReadableSize())
                    .setExportStatus(ExportStatusEnum.SUCCESS.getCode());
            exportInfoMapper.insert(exportInfo);
            exportInfo.setTaskNo(ExportProgressUtil.EXPORT_TASK_PREFIX+DateUtil.localDateToYyyymmdd(LocalDate.now())+"-"+exportInfo.getId());
            exportInfoMapper.updateById(exportInfo);
        } catch (ServiceException e) {
            // 用户主动取消下载不记为失败，避免误报
            if (ClientAbortUtil.isClientAbort(e)) {
                throw e;
            }
            progressConsumer.accept(-1);
            exportInfo.setQuantity(String.valueOf(baseDataList.size())).setExportStatus(ExportStatusEnum.FAILED.getCode());
            exportInfoMapper.insert(exportInfo);
            exportInfo.setTaskNo(ExportProgressUtil.EXPORT_TASK_PREFIX+DateUtil.localDateToYyyymmdd(LocalDate.now())+"-"+exportInfo.getId());
            exportInfoMapper.updateById(exportInfo);
            throw e;
        }
    }

    /**
     * 取文件或目录路径的最后一段，用于压缩包内图片命名
     */
    private static String lastPathSegment(String path) {
        if (CharSequenceUtil.isBlank(path)) {
            return "";
        }
        String normalized = path.replace("\\", "/");
        int index = normalized.lastIndexOf('/');
        String segment = index >= 0 ? normalized.substring(index + 1) : normalized;
        return CharSequenceUtil.isBlank(segment) ? "" : segment;
    }

    /**
     * 受电工状态：取该图片标记区域中的缺陷类型并去重拼接；无缺陷时回退为受电弓状态名称
     */
    private String pantographStatusText(String markedRegions, Integer pantographStatus) {
        Set<String> defectTypes = new LinkedHashSet<>();
        for (DefectMarkBO mark : parseMarkedRegions(markedRegions)) {
            if (null != mark && CharSequenceUtil.isNotBlank(mark.getDefectType())) {
                defectTypes.add(mark.getDefectType().trim());
            }
        }
        return defectTypes.isEmpty()
                ? PantographStatusEnum.getNameByCode(pantographStatus)
                : String.join("、", defectTypes);
    }

    @Override
    public Integer getConfirmStatusById(Long id) {
        return super.baseMapper.getConfirmStatusById(id);
    }
}