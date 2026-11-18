package com.nbk.smart5c.schedules;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nbk.smart5c.common.enums.AnalyzeStatusEnum;
import com.nbk.smart5c.common.utils.DirectoryCopyUtil;
import com.nbk.smart5c.common.utils.FileUtil;
import com.nbk.smart5c.common.utils.PortChecker;
import com.nbk.smart5c.common.websocket.WebSocketMsg;
import com.nbk.smart5c.common.websocket.WebSocketSessionManager;
import com.nbk.smart5c.entity.*;
import com.nbk.smart5c.mapper.InspectionDataMapper;
import com.nbk.smart5c.mapper.RealtimeAnalysisMapper;
import com.nbk.smart5c.mapper.TaskMapper;
import com.nbk.smart5c.pojo.bo.TaskPushSaveBO;
import com.nbk.smart5c.pojo.vo.CheckResult;
import com.nbk.smart5c.service.*;
import com.nbk.smart5c.service.Impl.InspectionDataServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

import static com.nbk.smart5c.service.Impl.InspectionDataServiceImpl.processMap;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class C5Schedule {

    /**
     * mountpath 下各段的目录名
     */
    private static final List<String> SEGMENT_DIRS = Arrays.asList("jinan", "weiguan", "qingdao");

    @Value("${inspect.basepath}")
    private String inspectDataPath;
    @Value("${inspect.mountpath}")
    private String mountpath;
    private final InspectionDataService<InspectionDataMapper, InspectionData> inspectionDataService;
    private final TaskService<TaskMapper, Task> taskService;
    private final InspectionDataMapper inspectionDataMapper;
    private volatile boolean applicationReady = false;
    private final Map<String, Long> blockMap = new HashMap<>();

    @Value("${ai.ip}")
    private String ip;
    @Value("${ai.port}")
    private Integer port;
    private final SectionService sectionService;
    private final LineService lineService;
    private final StationService stationService;
    private final DeviceService deviceService;
    private final RealtimeAnalysisService<RealtimeAnalysisMapper, RealtimeAnalysis> realtimeAnalysisService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        this.applicationReady = true;
        log.info("Application ready, scheduling tasks will start");
    }

    /**
     * AI分析文件
     */
//    @Async("taskExecutor")
//    @Scheduled(fixedDelay = 60*1000, initialDelay = 10*1000)
//    public void aiAnalysisFiles() {
//        if (!applicationReady) {
//            return; // 服务未就绪，跳过执行
//        }
//        LambdaQueryWrapper<InspectionData> inspectionDataWrapper = new LambdaQueryWrapper<>();
//        inspectionDataWrapper.eq(InspectionData::getAnalysisStatus, AnalyzeStatusEnum.WAIT_ANALYZE.getCode())
//                .last("limit 5");
//        List<InspectionData> inspectionDataList = inspectionDataService.list(inspectionDataWrapper);
//        if(CollUtil.isEmpty(inspectionDataList)){
//            return;
//        }
//        for(InspectionData inspectionData:inspectionDataList){
//            inspectionDataService.processImageValidation(new File(inspectDataPath+inspectionData.getLocomotiveImage()),inspectionData);
//        }
//    }

    /**
     * AI分析文件
     */
    @Async("taskExecutor")
    @Scheduled(fixedRate = 10 * 1000)
    public void checkProcessError() {
        if (!applicationReady) {
            return; // 服务未就绪，跳过执行
        }
        Set<Map.Entry<String, String>> entries = InspectionDataServiceImpl.processMap.entrySet();
        if (CollUtil.isEmpty(entries)) {
            return;
        }
        for (Map.Entry<String, String> entry : entries) {
            if ("0%".equals(entry.getValue()) && null == blockMap.get(entry.getKey())) {
                blockMap.put(entry.getKey(), System.currentTimeMillis());
            }
            if (MapUtil.isEmpty(blockMap)) {
                continue;
            }
            for (Map.Entry<String, Long> blockEntry : blockMap.entrySet()) {
                if ((System.currentTimeMillis() - blockEntry.getValue()) >= 10 * 1000 && "0%".equals(processMap.get(blockEntry.getKey()))) {
                    processMap.remove(blockEntry.getKey());
                }
            }
        }
    }

    /**
     * 定时扫描任务等待队列：系统中无分析中任务时，自动取出队列中下一个等待任务开始分析。
     * 兜底异步触发场景（服务重启、终态回调丢失等），保证队列中的任务最终会被消费。
     */
    @Scheduled(fixedRate = 10 * 1000, initialDelay = 15 * 1000)
    public void triggerTaskWaitQueue() {
        if (!applicationReady) {
            return;
        }
        try {
            taskService.triggerWaitQueue();
        } catch (Exception e) {
            log.error("定时触发任务等待队列异常: {}", e.getLocalizedMessage(), e);
        }
    }

    /**
     * 定时消费外部推送数据的解析队列：每 2 分钟一次，从队列取文件夹路径交给 5 个线程的线程池解析
     */
//    @Scheduled(fixedRate = 5 * 1000, initialDelay = 30 * 1000)
    public void consumePushParseQueue() {
        if (!applicationReady) {
            return;
        }
        try {
            taskService.consumePushParseQueue();
        } catch (Exception e) {
            log.error("定时消费推送解析队列异常: {}", e.getLocalizedMessage(), e);
        }
    }

    /**
     * 定时扫描无任务关联的巡查数据：task_id 为空且状态为待分析的数据，自动触发 AI 文件夹分析。
     */
    @Scheduled(fixedRate = 3 * 60 * 1000, initialDelay = 30 * 1000)
    public void analyzeOrphanInspectionData() {
        if (!applicationReady) {
            return;
        }
        try {
            LambdaQueryWrapper<InspectionData> wrapper = new LambdaQueryWrapper<>();
            wrapper.isNull(InspectionData::getTaskId)
                    .and(ia->ia.eq(InspectionData::getAnalysisStatus, AnalyzeStatusEnum.WAIT_ANALYZE.getCode()).or().isNull(InspectionData::getAnalysisStatus));
            List<InspectionData> inspectionDataList = inspectionDataService.list(wrapper);
            if (CollUtil.isEmpty(inspectionDataList)) {
                return;
            }
            // AI 服务端口未就绪时拒绝启动；放在排队判断之后，避免对排队中的任务做无意义的端口探测
            CheckResult checkResult = PortChecker.checkPort(ip, port, 500);
            if (!checkResult.getIsOpen()) {
                log.info("实时未分析数据定时扫描分析:算法服务未开启");
                return;
            }
            for (InspectionData inspectionData : inspectionDataList) {
                File file = new File(inspectDataPath + inspectionData.getLocomotiveImage());
                inspectionDataService.processFolderValidation(file, inspectionData);
            }
            Map<String, Object> taskStatusMap = new HashMap<>();
            taskStatusMap.put("refresh", "Y");
            Map<String, Long> newsCountMap = new HashMap<>();
            // 获取各用户最新推送的巡查数据统计
            for (Map.Entry<String, Long> entry : InspectionDataServiceImpl.userMaxInspectionIdCache.entrySet()) {
                if (null != entry.getValue()) {
                    long newCount = inspectionDataMapper.countGreatThanCurr(entry.getValue());
                    if(newCount==0){
                        continue;
                    }
                    newsCountMap.put(entry.getKey(), newCount);
                }
            }
            if(newsCountMap.isEmpty()){
                return;
            }
            log.info("schedule_newsCountMap: {}", newsCountMap);
            taskStatusMap.put("userNewsCount", newsCountMap);
            log.info("schedule_webSocketMsg_realtime_data_count: {}", taskStatusMap);
            WebSocketSessionManager.broadcastMsg(WebSocketMsg.msg("REALTIME_DATA_REFRESH", "PYTHON", taskStatusMap));
        } catch (Exception e) {
            log.error("定时扫描无任务巡查数据分析异常: {}", e.getLocalizedMessage(), e);
        }
    }

    /**
     * 每小时清理一次在nbk_inspection_data表中找不到的nbk_section，nbk_line，nbk_station，nbk_device，
     * 对应字段分别为section_id，line_id，station_id，device_id
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void cleanUnusedSectionsLinesStationsDevices() {
        if (!applicationReady) {
            return;
        }
        log.info("cleanUnusedSectionsLinesStationsDevices开始执行");
        try {
            cleanUnusedRecords("section_id", Section.class, sectionService);
            cleanUnusedRecords("line_id", Line.class, lineService);
            cleanUnusedRecords("station_id", Station.class, stationService);
            cleanUnusedRecords("device_id", Device.class, deviceService);
        } catch (Exception e) {
            log.error("清理未引用的基础数据失败", e);
        }
        log.info("cleanUnusedSectionsLinesStationsDevices执行完成");
    }

    /**
     * 引入实时分析表 nbk_realtime_analysis（source_filepath，dest_filepath，copy_status，parse_status 及审计字段），
     * 每3分钟扫描一次 mountpath 目录，目录下有jinan,weiguan,qingdao三个段文件夹，三个文件夹分别向下递归按当前年匹配年目录（如2026）、按当前月匹配月目录（如11）、按当前日匹配日目录（如03），
     * 目录匹配成功之后过滤掉该目录下的文件，只取目录：
     * <ul>
     *     <li>已在实时分析表中 parse_status 为 Y 的目录直接跳过</li>
     *     <li>数据目录 copy 到 ${inspect.basepath}/yyyyMMdd/段 下，同时清除其中文件名（不含扩展名）以 _md、_sl 结尾的图片</li>
     *     <li>目标目录已存在则跳过复制；复制失败或解析失败的目录保持未完成状态，下一轮继续处理</li>
     *     <li>调用 autoCreate 解析分析，处理结果（复制/解析状态）写入实时分析表</li>
     * </ul>
     * 另外每天 0 点清理实时分析表中创建时间超过 24 小时的数据。
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void scanNfsDirData() {
        if (!applicationReady) {
            return;
        }
        log.info("scanNfsDirData开始执行");
        try {
            LocalDate today = LocalDate.now();
            String year = String.valueOf(today.getYear());
            String month = String.format("%02d", today.getMonthValue());
            String day = String.format("%02d", today.getDayOfMonth());
            int dirCount = 0;
            int ignoredFileCount = 0;
            int successCount = 0;
            for (String segmentDir : SEGMENT_DIRS) {
                File segmentRoot = new File(mountpath, segmentDir);
                if (!segmentRoot.isDirectory()) {
                    log.info("scanNfsDirData未找到段目录: {}", segmentRoot.getAbsolutePath());
                    continue;
                }
                ScanResult result = scanSegmentDir(segmentRoot, segmentDir, year, month, day);
                dirCount += result.dirCount;
                ignoredFileCount += result.ignoredFileCount;
                successCount += result.successCount;
            }
            log.info("scanNfsDirData执行完成, 数据目录 {} 个, 忽略文件 {} 个, 本轮处理成功 {} 个",
                    dirCount, ignoredFileCount, successCount);
        } catch (Exception e) {
            log.error("scanNfsDirData扫描异常: {}", e.getLocalizedMessage(), e);
        }
    }

    /**
     * 扫描单个段目录：递归匹配 年/月/日 目录后，处理其下的数据目录
     */
    private ScanResult scanSegmentDir(File segmentRoot, String segmentDir, String year, String month, String day) {
        ScanResult result = new ScanResult();
        for (File dayDir : findDayDirs(segmentRoot, year, month, day)) {
            File[] dayEntries = dayDir.listFiles();
            if (ArrayUtil.isEmpty(dayEntries)) {
                continue;
            }
            // 过滤掉该目录下的文件，只取目录
            List<File> dataDirList = new ArrayList<>();
            for (File entry : dayEntries) {
                if (entry.isDirectory()) {
                    dataDirList.add(entry);
                } else {
                    result.ignoredFileCount++;
                }
            }
            result.dirCount += dataDirList.size();
            if (dataDirList.isEmpty()) {
                log.info("scanNfsDirData当天目录下无数据目录: {}, 忽略文件 {} 个", dayDir.getAbsolutePath(), result.ignoredFileCount);
                continue;
            }
            // 按目录名排序，保证处理顺序稳定
            dataDirList.sort(Comparator.comparing(File::getName));
            // 复制到 ${inspect.basepath}/yyyyMMdd/段 下，与 MQTT 同步目录保持一致，避免不同段的同名目录互相覆盖
            File copyParentDir = new File(new File(inspectDataPath, year + month + day), segmentDir);
            for (File dataDir : dataDirList) {
                // 实时分析表以 mountpath 下的源目录绝对路径为准
                String sourcePath = dataDir.getAbsolutePath();
                RealtimeAnalysis record = realtimeAnalysisService.queryBySourceFilepath(sourcePath);
                if (null != record && RealtimeAnalysis.STATUS_SUCCESS.equals(record.getParseStatus())) {
                    // 已解析成功的目录不再处理
                    continue;
                }
                File copiedDir = new File(copyParentDir, dataDir.getName());
                String copiedPath = copiedDir.getAbsolutePath();
                // 目标目录已存在则跳过复制，仅继续后续解析
                if (copiedDir.exists()) {
                    log.info("scanNfsDirData跳过已存在的目录: {}", copiedPath);
                } else {
                    try {
                        // 复制数据目录到 basepath 下，覆盖可能存在的历史残留
                        if (!DirectoryCopyUtil.copyDirectory(sourcePath, copiedPath, true)) {
                            log.error("scanNfsDirData复制数据目录失败: {} -> {}", sourcePath, copiedPath);
                            saveRealtimeAnalysis(record, sourcePath, copiedPath, RealtimeAnalysis.STATUS_FAIL, RealtimeAnalysis.STATUS_FAIL);
                            continue;
                        }
                    } catch (Exception e) {
                        log.error("scanNfsDirData复制数据目录异常: {}", sourcePath, e);
                        saveRealtimeAnalysis(record, sourcePath, copiedPath, RealtimeAnalysis.STATUS_FAIL, RealtimeAnalysis.STATUS_FAIL);
                        continue;
                    }
                }
                // 复制完成（本轮复制或目标已存在），清除复制目录中文件名以 _md、_sl 结尾的图片
                int deletedCount = FileUtil.deleteMdSlImages(copiedDir);
                // 复制成功但解析尚未完成；保存后回填 record，避免解析成功时重复插入
                record = saveRealtimeAnalysis(record, sourcePath, copiedPath, RealtimeAnalysis.STATUS_SUCCESS, RealtimeAnalysis.STATUS_FAIL);
                try {
                    TaskPushSaveBO pushParam = new TaskPushSaveBO();
                    pushParam.setFolderPath(copiedPath);
                    if (Boolean.TRUE.equals(taskService.autoCreate(pushParam))) {
                        // 解析成功，标记 parse_status=Y，后续扫描不再处理
                        saveRealtimeAnalysis(record, sourcePath, copiedPath, RealtimeAnalysis.STATUS_SUCCESS, RealtimeAnalysis.STATUS_SUCCESS);
                        result.successCount++;
                        log.info("scanNfsDirData处理成功: {}, 删除 _md/_sl 图片 {} 张", copiedPath, deletedCount);
                    }
                } catch (Exception e) {
                    log.error("scanNfsDirData解析目录异常: {}", copiedPath, e);
                }
            }
        }
        return result;
    }

    /**
     * 在段目录下递归匹配当前 年/月/日 三级目录，返回所有匹配到的日目录
     */
    private List<File> findDayDirs(File segmentRoot, String year, String month, String day) {
        List<File> dayDirs = new ArrayList<>();
        // 年目录可能位于段目录下若干层（如 place/2026），月、日目录为其直接下级
        for (File yearDir : findDirsByName(segmentRoot, year, 4)) {
            for (File monthDir : findDirsByName(yearDir, month, 1)) {
                dayDirs.addAll(findDirsByName(monthDir, day, 1));
            }
        }
        return dayDirs;
    }

    private List<File> findDirsByName(File root, String name, int depth) {
        List<File> matched = new ArrayList<>();
        File[] children = root.listFiles(File::isDirectory);
        if (ArrayUtil.isEmpty(children)) {
            return matched;
        }
        for (File child : children) {
            if (name.equals(child.getName())) {
                matched.add(child);
            } else if (depth > 1) {
                matched.addAll(findDirsByName(child, name, depth - 1));
            }
        }
        return matched;
    }

    /**
     * 单轮扫描的统计结果
     */
    private static class ScanResult {
        private int dirCount;
        private int ignoredFileCount;
        private int successCount;
    }

    /**
     * 写入/更新实时分析表中源目录对应的记录。record 为空时新增，否则按 id 更新状态。
     *
     * @return 持久化后的记录（新增后 id 已回填）
     */
    private RealtimeAnalysis saveRealtimeAnalysis(RealtimeAnalysis record, String sourcePath, String destPath,
                                                  String copyStatus, String parseStatus) {
        if (null == record) {
            record = new RealtimeAnalysis();
            record.setSourceFilepath(sourcePath)
                    .setDestFilepath(destPath)
                    .setCopyStatus(copyStatus)
                    .setParseStatus(parseStatus);
            realtimeAnalysisService.save(record);
            return record;
        }
        record.setDestFilepath(destPath).setCopyStatus(copyStatus).setParseStatus(parseStatus);
        realtimeAnalysisService.updateById(record);
        return record;
    }

    /**
     * 每天 0 点清理实时分析表中创建时间超过 24 小时的记录
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanRealtimeAnalysis() {
        if (!applicationReady) {
            return;
        }
        try {
            long removed = realtimeAnalysisService.removeExpired(24);
            log.info("cleanRealtimeAnalysis清理实时分析表完成, 删除 {} 条", removed);
        } catch (Exception e) {
            log.error("cleanRealtimeAnalysis清理实时分析表异常: {}", e.getLocalizedMessage(), e);
        }
    }

    /**
     * 删除 target 表中未被 nbk_inspection_data.fkColumn 引用的记录
     */
    private <M extends BaseMapper<T>, T> void cleanUnusedRecords(String fkColumn, Class<T> clazz,
                                                                 IService<T> targetService) {
        QueryWrapper<InspectionData> wrapper = new QueryWrapper<>();
        wrapper.select("distinct " + fkColumn).isNotNull(fkColumn);
        List<Object> usedIds = inspectionDataService.listObjs(wrapper);
        if (CollUtil.isEmpty(usedIds)) {
            usedIds = Collections.singletonList(-1L);
        }
        QueryWrapper<T> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.notIn("id", usedIds);
        long count = targetService.count(deleteWrapper);
        if (count > 0) {
            targetService.remove(deleteWrapper);
        }
        log.info("清理{}未引用记录完成，删除 {} 条，剩余引用数: {}", clazz.getSimpleName(), count, usedIds.size());
    }

}
