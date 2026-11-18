package com.nbk.smart5c.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.nbk.smart5c.common.enums.*;
import com.nbk.smart5c.common.utils.*;
import com.nbk.smart5c.common.utils.holder.CurrentUserHolder;
import com.nbk.smart5c.common.websocket.WebSocketMsg;
import com.nbk.smart5c.common.websocket.WebSocketSessionManager;
import com.nbk.smart5c.entity.*;
import com.nbk.smart5c.entity.Task;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.mapper.*;
import com.nbk.smart5c.pojo.bo.*;
import com.nbk.smart5c.pojo.qo.TaskQO;
import com.nbk.smart5c.pojo.qo.TaskReviewQO;
import com.nbk.smart5c.pojo.vo.*;
import com.nbk.smart5c.service.InspectionDataService;
import com.nbk.smart5c.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务表业务逻辑层
 * @since 2026-04-07 16:41:49
 */
@Log4j2
@Service
@Primary
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService<TaskMapper, Task> {

    private final static String FILE_NAME = "nbk_task(任务表)";

    private final static List<String> TITLE = Arrays.asList(
            "id(主键)",
            "task_name(任务名)",
            "task_folder(任务文件夹)",
            "priority(0:低,1:中,2:高)",
            "task_status(任务状态(0:待分析,1:分析中,2:暂停,分析完成))",
            "analysis_progress(分析进度)",
            "in_storage(是否入库(0:未入库,1:已入库))",
            "file_size(文件大小)",
            "ext_name(扩展名)",
            "file_nums(文件数量)",
            "import_nums(已导入文件数量)",
            "analyzed_duration(分析时长(秒数))",
            "analyzed_pictures(已分析图片数)",
            "data_type(数据类型(0:图片,1:视频))",
            "item_points(项点)",
            "ai_alarms(AI报警数)",
            "manual_progress(人工审核进度)",
            "checkout_rate(检出率(检出真实缺陷数/真实缺陷总数))",
            "reporting_rate(提报率(智能识别疑似缺陷数/总检测图片数))",
            "hit_rate(准确率)",
            "created_user(创建人)",
            "created_time(创建时间)",
            "updated_user(修改人)",
            "updated_time(修改时间)"
    );

    @Value("${inspect.basepath}")
    private String inspectDataPath;
    @Value("${inspect.host}")
    private String inspectHost;
    @Value("${inspect.defect}")
    private String inspectDefect;
    @Value("${inspect.defectpath}")
    private String defectpath;
    @Lazy
    private final InspectionDataService<InspectionDataMapper, InspectionData> inspectionDataService;
    private final InspectionDataMapper inspectionDataMapper;
    private final TaskFileMapper taskFileMapper;
    private final InspectionReviewResultMapper inspectionReviewResultMapper;
    private final LineMapper lineMapper;
    private final StationMapper stationMapper;
    private final DeviceMapper deviceMapper;
    private final SectionMapper sectionMapper;
    private final BureauMapper bureauMapper;
    private final DefectStorageMapper defectStorageMapper;
    private final ApiUtils apiUtils;
    @Autowired
    @Qualifier("taskExecutor")
    private ScheduledExecutorService taskScheduler;
    @Autowired
    @Qualifier("pushParseExecutor")
    private ExecutorService pushParseExecutor;
    private static final Map<String, Object> TASK_MONITOR_MAP = new ConcurrentHashMap<>();
    /**
     * 任务等待队列：当系统中已有分析中的任务时，新启动的任务会进入该队列等待。
     * 前面的任务分析完成或暂停后，队列中先进队列的任务自动开始分析。
     */
    private static final Queue<Long> TASK_WAIT_QUEUE = new ConcurrentLinkedQueue<>();
    /**
     * 外部推送实时分析的文件夹队列：请求线程只负责入队并立即返回，由定时任务消费解析，避免阻塞推送方。
     */
    private static final Queue<String> PUSH_PARSE_QUEUE = new ConcurrentLinkedQueue<>();
    /**
     * 已入队或正在解析的文件夹路径，用于去重
     */
    private static final Set<String> PUSH_PARSE_PENDING = ConcurrentHashMap.newKeySet();
    /**
     * 推送解析线程池大小，同时限制消费时的在途任务数
     */
    private static final int PUSH_PARSE_POOL_SIZE = 5;
    private final AtomicInteger pushParseInFlight = new AtomicInteger();
    /**
     * 解析阶段的互斥锁：Section/Line/Station/Device 的“查不到就插入”在多线程下会并发写入重复主数据，
     * 因此解析串行执行，耗时的 AI 分析阶段仍由线程池并发处理。
     */
    private static final Object PUSH_PARSE_LOCK = new Object();

    /**
     * 新增数据
     * <p>
     * 先以"创建中(0)"作为中间状态落库并立即返回，避免在请求线程内同步解析大量文件导致响应缓慢；
     * 文件解析、文件数量统计、任务文件关联等逻辑通过 {@link #asyncParseTaskData(Long, List)} 异步处理，
     * 全部完成后将任务状态更新为"待分析(1)"。
     *
     * @param info 新增数据信息
     */
    /**
     * 新增数据
     * <p>
     * 先以"创建中(0)"作为中间状态落库并立即返回，避免在请求线程内同步解析大量文件导致响应缓慢；
     * 文件解析、文件数量统计、任务文件关联等逻辑通过 {@link #asyncParseTaskData(Long, List)} 异步处理，
     * 全部完成后将任务状态更新为"待分析(1)"。
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(TaskSaveBO info) throws ServiceException {
        Task entity = new Task();
        BeanUtils.copyProperties(info, entity);
        entity.setDeadline(info.getDeadline());
        entity.setAnalysisProgress("0%");
        if (StrUtil.isBlank(info.getDataPath())) {
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "请选择数据进行分析");
        }
        File dataFolder = new File(info.getDataPath());
        if(!dataFolder.exists()){
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "选择的数据路径不存在，请重新选择数据进行分析");
        }
        entity.setTaskName(DateUtil.getCurrentY_m_d_H_m_s(LocalDateTime.now()) + "_" + dataFolder.getName());
        // 先以"创建中"状态落库，前端可据此展示任务正在初始化
        entity.setTaskStatus(Task.TASK_STATUS_CREATING);
        entity.setLiving(LogicStatusEnum.NO.getCode());
        super.save(entity);
        Long taskId = entity.getId();
        File[] allFileArr = dataFolder.listFiles();
        if (ArrayUtil.isEmpty(allFileArr)) {
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "没有可分析的文件");
        }
        List<String> filePaths = Arrays.stream(allFileArr)
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(info.getTaskFiles())){
            Set<String> selectedPaths = info.getTaskFiles().stream()
                    .map(p -> new File(p).getAbsolutePath())
                    .collect(Collectors.toSet());
            filePaths = filePaths.stream()
                    .filter(p -> !selectedPaths.contains(p))
                    .collect(Collectors.toList());
        }
        if (CollUtil.isEmpty(filePaths)) {
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "没有可分析的文件");
        }
        List<String> finalFilePaths = filePaths;
        taskScheduler.submit(() -> {
            try {
                asyncParseTaskData(taskId, finalFilePaths);
            } catch (Exception e) {
                log.error("异步解析任务数据异常, taskId={}", taskId, e);
            }
        });
        return true;
    }

    /**
     * 异步解析任务关联数据：逐个文件解析、统计文件数、写入任务文件关联，
     * 完成后将任务状态由"创建中(0)"切换为"待分析(1)"。
     */
    private void asyncParseTaskData(Long taskId, List<String> filePaths) {
        List<TaskFile> taskFiles = new ArrayList<>(filePaths.size());
        try {
            for (String filePath : filePaths) {
                try {
                    inspectionDataService.parseData(taskId, filePath);
                } catch (ServiceException e) {
                    log.error("解析任务数据失败, taskId={}, filePath={}, err={}", taskId, filePath, e.getLocalizedMessage());
                    throw e;
                }
                TaskFile taskFile = new TaskFile();
                taskFile.setTaskId(taskId).setFilePath(filePath);
                taskFiles.add(taskFile);
            }
            Task task = super.getById(taskId);
            if (null == task) {
                return;
            }
            task.setFileNums(inspectionDataMapper.countByTaskId(taskId));
            task.setTaskStatus(Task.TASK_STATUS_WAIT_ANALYSIS);
            super.updateById(task);
            if (CollUtil.isNotEmpty(taskFiles)) {
                taskFileMapper.insert(taskFiles);
            }
            Map<String, String> taskStatusMap = new HashMap<>();
            taskStatusMap.put("refresh", "Y");
            WebSocketSessionManager.broadcastMsg(WebSocketMsg.msg("TASK_CREATE_PROGRESS", apiUtils.currentUserId() + "", taskStatusMap));
        } catch (Exception e) {
            log.error("任务数据解析失败, taskId={}", taskId, e);
            Task task = super.getById(taskId);
            if (null != task) {
                task.setTaskStatus(Task.TASK_STATUS_WAIT_ANALYSIS);
                super.updateById(task);
            }
        }
    }

    /**
     * 异步推送解析任务关联数据：逐个文件解析、统计文件数、写入任务文件关联，判断文件夹及文件夹下所有的图片有没有分析过，没有分析过或没有完全分析，需要带入任务重新分析
     * 完成后将任务状态由"创建中(0)"切换为"待分析(1)"。
     */
    private void asyncPushParseTaskData(Long taskId, List<String> filePaths) {
        List<TaskFile> taskFiles = new ArrayList<>(filePaths.size());
        try {
            for (String filePath : filePaths) {
                String relativePath = filePath.replace(inspectDataPath, "");
                long folderCount = inspectionDataMapper.countByRelativePath(relativePath);
                if (folderCount == 0) {
                    try {
                        inspectionDataService.parseData(taskId, filePath);
                    } catch (ServiceException e) {
                        log.error("解析任务数据失败, taskId={}, filePath={}, err={}", taskId, filePath, e.getLocalizedMessage());
                        throw e;
                    }
                    TaskFile taskFile = new TaskFile();
                    taskFile.setTaskId(taskId).setFilePath(filePath);
                    taskFiles.add(taskFile);
                } else {
                    long inspectionId = inspectionDataMapper.getIdByRelativePath(relativePath);
                    long dtlCount = inspectionReviewResultMapper.countByInspectionId(inspectionId);
                    File[] imageFiles = new File(filePath).listFiles();
                    if (ArrayUtil.isEmpty(imageFiles)) continue;
                    List<File> imageFileList = Arrays.stream(imageFiles).filter(FileUtil::judgeImageFile).collect(Collectors.toList());
                    if (imageFileList.size() != dtlCount) {
                        try {
                            inspectionDataService.parseData(taskId, filePath);
                        } catch (ServiceException e) {
                            log.error("解析任务数据失败, taskId={}, filePath={}, err={}", taskId, filePath, e.getLocalizedMessage());
                            throw e;
                        }
                        TaskFile taskFile = new TaskFile();
                        taskFile.setTaskId(taskId).setFilePath(filePath);
                        taskFiles.add(taskFile);
                    }

                }
            }
            Task task = super.getById(taskId);
            if (null == task) {
                return;
            }
            if (CollUtil.isNotEmpty(taskFiles)) {
                taskFileMapper.insert(taskFiles);
            } else {
                super.removeById(task);
                return;
            }
            task.setFileNums(inspectionDataMapper.countByTaskId(taskId));
            task.setTaskStatus(Task.TASK_STATUS_WAIT_ANALYSIS);
            super.updateById(task);
            //开始自动分析
            startAnalysis(taskId);
            Map<String, String> taskStatusMap = new HashMap<>();
            taskStatusMap.put("refresh", "Y");
            WebSocketSessionManager.broadcastMsg(WebSocketMsg.msg("TASK_CREATE_PROGRESS", apiUtils.currentUserId() + "", taskStatusMap));
        } catch (Exception e) {
            log.error("任务数据解析失败, taskId={}", taskId, e);
            Task task = super.getById(taskId);
            if (null != task) {
                task.setTaskStatus(Task.TASK_STATUS_WAIT_ANALYSIS);
                super.updateById(task);
            }
        }
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public TaskEditVO queryById(Long id) {
        Task task = super.getById(id);
        TaskEditVO taskEditVO = new TaskEditVO();
        taskEditVO.setId(task.getId()).setPriority(task.getPriority()).setDeadline(task.getDeadline());
        List<String> taskFiles = taskFileMapper.getByTaskId(id);
        taskEditVO.setTaskFiles(taskFiles);
        return taskEditVO;
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<TaskVO> queryAndPaging(TaskQO query) {
        MPJLambdaWrapper<Task> taskMPJBaseMapper = new MPJLambdaWrapper<>(Task.class);
        taskMPJBaseMapper.like(CharSequenceUtil.isNotBlank(query.getTaskName()), Task::getTaskName, query.getTaskName());
        taskMPJBaseMapper.eq(Objects.nonNull(query.getPriority()), Task::getPriority, query.getPriority());
        taskMPJBaseMapper.eq(Objects.nonNull(query.getTaskStatus()), Task::getTaskStatus, query.getTaskStatus());
        taskMPJBaseMapper.ge(Objects.nonNull(query.getStartTime()), Task::getEndCheckTime, query.getStartTime());
        taskMPJBaseMapper.le(Objects.nonNull(query.getEndTime()), Task::getEndCheckTime, query.getEndTime());
        if (Objects.nonNull(query.getReviewStatus())) {
            if (query.getReviewStatus() == 0) {
                taskMPJBaseMapper.and(wrapper -> wrapper.isNull(Task::getReviewNums)
                        .or()
                        .apply("review_nums < file_nums"));
            } else if (query.getReviewStatus() == 1) {
                taskMPJBaseMapper.isNotNull(Task::getReviewNums)
                        .apply("review_nums >= file_nums");
            }
        }
        taskMPJBaseMapper.orderByDesc(Task::getPriority).orderByDesc(Task::getCreatedTime);
        Page<TaskVO> page = taskMPJBaseMapper.page(new Page<>(query.getPageNo(), query.getPageSize()), TaskVO.class);
        if (CollUtil.isNotEmpty(page.getRecords())) {
            List<Long> taskIds = page.getRecords().stream().map(TaskVO::getId).collect(Collectors.toList());
            Map<Long, Long> confirmDefectMap = batchCountConfirmDefect(taskIds);
            LocalDateTime now = LocalDateTime.now();
            for (TaskVO taskVO : page.getRecords()) {
                taskVO.setTaskStatusText(TaskStatusEnum.getNameByCode(taskVO.getTaskStatus()));
                taskVO.setConfirmDefectNum(confirmDefectMap.getOrDefault(taskVO.getId(), 0L));
                taskVO.setReviewProgress(NumberUtil.getTwoNumberDivideToPercent(taskVO.getReviewNums(), taskVO.getFileNums()));
                if (isTaskTimeout(taskVO, now)) {
                    taskVO.setTimeout(true);
                }
            }
        }
        return page;
    }

    private Map<Long, Long> batchCountConfirmDefect(List<Long> taskIds) {
        if (CollUtil.isEmpty(taskIds)) {
            return Collections.emptyMap();
        }
        List<Map<String, Object>> resultList = inspectionReviewResultMapper.batchCountConfirmDefect(taskIds);
        Map<Long, Long> map = new HashMap<>(resultList.size());
        for (Map<String, Object> row : resultList) {
            Long taskId = ((Number) row.get("task_id")).longValue();
            Long cnt = ((Number) row.get("cnt")).longValue();
            map.put(taskId, cnt);
        }
        return map;
    }

    private boolean isTaskTimeout(TaskVO taskVO, LocalDateTime now) {
        if (null == taskVO.getDeadline() || !taskVO.getDeadline().isBefore(now)) {
            return false;
        }
        Integer status = taskVO.getTaskStatus();
        if (TaskStatusEnum.CREATING.getCode().equals(status)
                || TaskStatusEnum.WAIT_CHECK.getCode().equals(status)) {
            return true;
        }
        return null == taskVO.getReviewNums()
                || (null != taskVO.getFileNums() && taskVO.getFileNums() > taskVO.getReviewNums());
    }


    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<TaskVO> queryAll(TaskQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(TaskUpdateBO info) {
        Task entity = super.getById(info.getId());
        ObjectCopyTool.copyPropertiesNotNullandEmpty(info, entity);
        entity.setDeadline(info.getDeadline());
        if (CollUtil.isEmpty(info.getTaskFiles())) {
            super.updateById(entity);
        } else {
            // 删除任务关联的分析目录
            taskFileMapper.deleteByTaskId(entity.getId());
            // 删除任务关联的解析数据
            inspectionDataMapper.deleteByTaskId(entity.getId());
            entity.setTaskName(DateUtil.getCurrentY_m_d_H_m_s(LocalDateTime.now()) + "_" + new File(info.getTaskFiles().get(0)).getParentFile().getName());
            // 先以"创建中"状态落库，前端可据此展示任务正在初始化
            entity.setTaskStatus(Task.TASK_STATUS_CREATING);
            Long taskId = entity.getId();
            List<String> filePaths = new ArrayList<>(info.getTaskFiles());
            taskScheduler.submit(() -> {
                try {
                    asyncParseTaskData(taskId, filePaths);
                } catch (Exception e) {
                    log.error("异步解析任务数据异常, taskId={}", taskId, e);
                }
            });
            super.updateById(entity);
        }
        return true;
    }

    /**
     * 数据导出
     *
     * @param query 查询条件
     */
    @Override
    public void export(TaskQO query, HttpServletResponse response) {
        // 查询用户数据
        List<TaskVO> result = baseMapper.queryAll(query);
        List<List<String>> data = new ArrayList<>(result.size());
        for (TaskVO item : result) {
            data.add(Arrays.asList(
//                item.getId() + "",
//                item.getTaskName(),
//                item.getTaskFolder(),
//                item.getPriority() + "",
//                item.getTaskStatus() + "",
//                item.getAnalysisProgress() + "",
//                item.getInStorage() + "",
//                item.getFileSize() + "",
//                item.getExtName(),
//                item.getFileNums(),
//                item.getImportNums() + "",
//                item.getAnalyzedDuration() + "",
//                item.getAnalyzedFolders() + "",
//                item.getDataType() + "",
//                item.getItemPoints(),
//                item.getAiAlarms() + "",
//                item.getReviewNums() + "",
//                item.getCheckoutRate() + "",
//                item.getReportingRate() + "",
//                item.getHitRate() + "",
//                item.getCreatedUser(),
//                Objects.isNull(item.getCreatedTime())?"":item.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
//                item.getUpdatedUser(),
//                Objects.isNull(item.getUpdatedTime())?"":item.getUpdatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
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

    @Value("${ai.ip}")
    private String ip;
    @Value("${ai.port}")
    private Integer port;

    @Override
    public Boolean startAnalysis(Long taskId) throws ServiceException {
        // 任务存在性校验前置：避免对非法 taskId 执行端口探测或入队等无用逻辑
        if (null == super.getById(taskId)) {
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "任务不存在");
        }
        // 系统中有分析中的任务，当前任务进入等待队列，状态置为等待中(9)
        if (super.baseMapper.getJudgeAnalyzingTask(taskId) > 0) {
            // 仅更新 task_status，避免 updateById 全字段回写覆盖 asyncAiCheck 并发推进的进度字段
            LambdaUpdateWrapper<Task> waitQueueUpdate = new LambdaUpdateWrapper<Task>()
                    .eq(Task::getId, taskId)
                    .set(Task::getTaskStatus, Task.TASK_STATUS_WAIT_QUEUE);
            super.update(waitQueueUpdate);
            TASK_WAIT_QUEUE.offer(taskId);
            return true;
        }
        // AI 服务端口未就绪时拒绝启动；放在排队判断之后，避免对排队中的任务做无意义的端口探测
        CheckResult checkResult = PortChecker.checkPort(ip, port, 500);
        if (!checkResult.getIsOpen()) {
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "算法服务未开启");
        }
        return doStartAnalysis(taskId);
    }

    /**
     * 实际启动任务分析的逻辑：清理暂停标记、更新开始时间、查询待分析数据并提交异步分析。
     * 队列触发与直接启动均复用该方法。
     */
    private Boolean doStartAnalysis(Long taskId) throws ServiceException {
        TASK_MONITOR_MAP.remove(taskId + "_status");
        Task task = super.getById(taskId);
        if (null == task) {
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "任务不存在");
        }
        super.baseMapper.updateTaskStartTime(taskId);//如果开始分析时间为空，更新开始分析时间
        LambdaQueryWrapper<InspectionData> inspectionQueryWrapper = new LambdaQueryWrapper<>();
        inspectionQueryWrapper.eq(InspectionData::getTaskId, taskId).eq(InspectionData::getAnalysisStatus, InspectionData.ANALYSIS_STATUS_WAIT_A);
        List<InspectionData> inspectionDataList = inspectionDataService.list(inspectionQueryWrapper);
        if (CollUtil.isEmpty(inspectionDataList)) {//如果没有可分析数据，那么任务变成分析完成
            task.setTaskStatus(Task.TASK_STATUS_FINISH_ANALYSIS);
            LocalDateTime finishTime = LocalDateTime.now();
            long durationSeconds;
            if (null != task.getPauseTime()) {
                durationSeconds = Duration.between(task.getPauseTime(), finishTime).getSeconds();
                task.setAnalyzedDuration(task.getAnalyzedDuration() == 0 ? durationSeconds : durationSeconds + task.getAnalyzedDuration());
            } else {
                durationSeconds = Duration.between(task.getStartCheckTime(), finishTime).getSeconds();
                task.setAnalyzedDuration(durationSeconds);
            }
            task.setEndCheckTime(finishTime);
            // 完成时强制 100% 且 analyzedFolders 对齐 fileNums，自愈暂停期间"inspection 已处理但计数未+1"造成的漂移
            task.setAnalysisProgress("100%");
            if (null != task.getFileNums()) {
                task.setAnalyzedFolders(task.getFileNums());
            }
            super.updateById(task);
            // 当前任务无可分析数据直接完成，触发队列中下一个等待任务
            tryStartNextQueuedTask();
            return true;
        } else {//变成分析中
            LambdaUpdateWrapper<Task> resumeUpdate = new LambdaUpdateWrapper<Task>()
                    .eq(Task::getId, taskId)
                    .set(Task::getTaskStatus, Task.TASK_STATUS_ANALYSISING)
                    .set(Task::getPauseTime, (LocalDateTime) null);
            super.update(resumeUpdate);
            task = super.getById(taskId);
        }
        Task finalTask = task;
        taskScheduler.submit(() -> asyncAiCheck(finalTask, inspectionDataList));
        return true;
    }

    /**
     * 从等待队列中按先进先出原则取出下一个任务并启动分析。
     * 任务状态非"等待中"时（已被删除/暂停/取消等）跳过并继续尝试下一个。
     */
    private void tryStartNextQueuedTask() {
        Long nextTaskId = TASK_WAIT_QUEUE.poll();
        if (null == nextTaskId) {
            return;
        }
        Task nextTask = super.getById(nextTaskId);
        if (null == nextTask || !Integer.valueOf(Task.TASK_STATUS_WAIT_QUEUE).equals(nextTask.getTaskStatus())) {
            // 任务已不存在或状态已变更，跳过并尝试下一个
            tryStartNextQueuedTask();
            return;
        }
        try {
            doStartAnalysis(nextTaskId);
        } catch (ServiceException e) {
            log.error("启动队列中下一个任务分析失败, taskId={}", nextTaskId, e);
            tryStartNextQueuedTask();
        }
    }

    /**
     * 触发等待队列：当系统中无分析中任务且队列非空时，取出下一个等待任务开始分析。
     * 由定时任务调用，保证服务重启或异常中断后队列中的任务也能被自动消费。
     */
    @Override
    public boolean triggerWaitQueue() {
        if (TASK_WAIT_QUEUE.isEmpty()) {
            return false;
        }
        // 仍存在分析中任务时，等待其完成或暂停后再触发
        long analyzingCount = super.baseMapper.countAnalyzingTask();
        if (analyzingCount > 0) {
            return false;
        }
        tryStartNextQueuedTask();
        return true;
    }

    public void asyncAiCheck(Task task, List<InspectionData> inspectionDataList) {
        Long taskId = task.getId();
        for (InspectionData inspectionData : inspectionDataList) {
            // 循环顶部统一用 TASK_MONITOR_MAP 判断暂停；不再读内存 task.getTaskStatus()（其值在循环内从不刷新，恒为 ANALYSISING）
            if (isTaskPaused(taskId)) {
                break;
            }
            File file = new File(inspectDataPath + inspectionData.getLocomotiveImage());
            inspectionDataService.processFolderValidation(file, inspectionData);
            // processFolderValidation 耗时较长，期间可能发生 pauseAnalysis；写库前再次确认，避免把 PAUSE 覆盖回 ANALYSISING
            if (isTaskPaused(taskId)) {
                break;
            }
            try {
                Map<String, Object> taskStatusMap = new HashMap<>();
                taskStatusMap.put("taskId", taskId);
                int checkTotal = task.getAnalyzedFolders() == null ? 0 : task.getAnalyzedFolders() + 1;
                LocalDateTime now = LocalDateTime.now();
                long durationSeconds = calcAnalyzedDuration(task, now);
                if (checkTotal >= task.getFileNums()) {//如果所有文件都检查完毕，那么任务变成分析完成
                    // 仅在仍为分析中时才推进到完成；带 taskStatus 条件的更新天然避免与并发 pauseAnalysis 相互覆盖
                    LambdaUpdateWrapper<Task> finishUpdate = new LambdaUpdateWrapper<Task>()
                            .eq(Task::getId, taskId)
                            .eq(Task::getTaskStatus, Task.TASK_STATUS_ANALYSISING)
                            .set(Task::getTaskStatus, Task.TASK_STATUS_FINISH_ANALYSIS)
                            .set(Task::getAnalyzedFolders, checkTotal)
                            .set(Task::getAnalysisProgress, "100%")
                            .set(Task::getAnalyzedDuration, durationSeconds)
                            .set(Task::getEndCheckTime, now);
                    super.update(finishUpdate);
                    task.setAnalyzedFolders(checkTotal);
                    task.setAnalysisProgress("100%");
                    task.setAnalyzedDuration(durationSeconds);
                    task.setEndCheckTime(now);
                    task.setTaskStatus(Task.TASK_STATUS_FINISH_ANALYSIS);
                    taskStatusMap.put("analysisProgress", "100%");
                    WebSocketSessionManager.broadcastMsg(WebSocketMsg.msg("TASK_CHECK_PROGRESS", apiUtils.currentUserId() + "", taskStatusMap));
                    break;
                } else {
                    String analysisProgress = NumberUtil.getTwoNumberDivideToPercent(checkTotal, task.getFileNums());
                    // 选择性更新：只写进度相关字段，绝不触碰 taskStatus，从根本上消除与 pauseAnalysis 的覆盖竞态
                    LambdaUpdateWrapper<Task> progressUpdate = new LambdaUpdateWrapper<Task>()
                            .eq(Task::getId, taskId)
                            .set(Task::getAnalyzedFolders, checkTotal)
                            .set(Task::getAnalysisProgress, analysisProgress)
                            .set(Task::getAnalyzedDuration, durationSeconds);
                    super.update(progressUpdate);
                    task.setAnalyzedFolders(checkTotal);
                    task.setAnalysisProgress(analysisProgress);
                    task.setAnalyzedDuration(durationSeconds);
                    taskStatusMap.put("analysisProgress", analysisProgress);
                    WebSocketSessionManager.broadcastMsg(WebSocketMsg.msg("TASK_CHECK_PROGRESS", apiUtils.currentUserId() + "", taskStatusMap));
                }
            } catch (Exception e) {
                log.error("分析异常:{}", e.getLocalizedMessage());
            }
        }
        LambdaQueryWrapper<InspectionData> inspectionQueryWrapper = new LambdaQueryWrapper<>();
        inspectionQueryWrapper.eq(InspectionData::getTaskId, taskId).eq(InspectionData::getAnalysisStatus, InspectionData.ANALYSIS_STATUS_WAIT_A);
        inspectionDataList = inspectionDataService.list(inspectionQueryWrapper);
        // 收尾判断必须基于 DB 实时状态，不能用循环顶部传入的内存 task（其 taskStatus 仍是 ANALYSISING，会让暂停分支永远走不到）
        Task latest = super.getById(taskId);
        if (null == latest) {
            return;
        }
        boolean paused = isTaskPaused(taskId) || Integer.valueOf(Task.TASK_STATUS_PAUSE).equals(latest.getTaskStatus());
        if (paused) {
            // 任务已被暂停，触发队列中下一个等待任务开始分析
            tryStartNextQueuedTask();
            return;
        }
        if (CollUtil.isEmpty(inspectionDataList) || (null != latest.getAnalyzedFolders() && null != latest.getFileNums() && latest.getAnalyzedFolders() >= latest.getFileNums())) {//如果没有可分析数据，那么任务变成分析完成
            LocalDateTime finishTime = LocalDateTime.now();
            long durationSeconds = calcAnalyzedDuration(latest, finishTime);
            LambdaUpdateWrapper<Task> finishUpdate = new LambdaUpdateWrapper<Task>()
                    .eq(Task::getId, taskId)
                    .eq(Task::getTaskStatus, Task.TASK_STATUS_ANALYSISING)
                    .set(Task::getTaskStatus, Task.TASK_STATUS_FINISH_ANALYSIS)
                    .set(Task::getAnalyzedDuration, durationSeconds)
                    .set(Task::getEndCheckTime, finishTime)
                    // 完成时强制 100% 且 analyzedFolders 对齐 fileNums，自愈暂停期间"inspection 已处理但计数未+1"造成的漂移
                    .set(Task::getAnalysisProgress, "100%")
                    .set(Task::getAnalyzedFolders, latest.getFileNums());
            super.update(finishUpdate);
            Map<String, Object> taskStatusMap = new HashMap<>();
            taskStatusMap.put("taskId", taskId);
            taskStatusMap.put("analysisProgress", "100%");
            WebSocketSessionManager.broadcastMsg(WebSocketMsg.msg("TASK_CHECK_PROGRESS", apiUtils.currentUserId() + "", taskStatusMap));
            // 当前任务分析完成，触发队列中下一个等待任务
            tryStartNextQueuedTask();
        }
    }

    /**
     * 判断任务是否处于暂停状态：以 TASK_MONITOR_MAP 标志为准（pauseAnalysis 在写库前即置位）。
     * 不读 task.getTaskStatus()，因为 asyncAiCheck 持有的 task 是入参快照，循环内不刷新，无法反映并发暂停。
     */
    private boolean isTaskPaused(Long taskId) {
        Object status = TASK_MONITOR_MAP.get(taskId + "_status");
        return null != status && "pause".equals(status.toString());
    }

    /**
     * 计算累计分析时长：
     * - 首次运行（pauseTime 为空）：自 startCheckTime 至 now
     * - 恢复后运行（pauseTime 非空）：已累计时长 + 自 pauseTime 至 now
     * 与 pauseAnalysis 的口径保持一致，避免在 asyncAiCheck 中重置 analyzedDuration。
     */
    private long calcAnalyzedDuration(Task task, LocalDateTime now) {
        long existing = null != task.getAnalyzedDuration() ? task.getAnalyzedDuration() : 0L;
        if (null != task.getPauseTime()) {
            return existing + Duration.between(task.getPauseTime(), now).getSeconds();
        }
        if (null != task.getStartCheckTime()) {
            return Duration.between(task.getStartCheckTime(), now).getSeconds();
        }
        return existing;
    }

    @Override
    public Page<TaskFileVO> listChildFile(String filePath, Integer pageNo, Integer pageSize) {
        File rootFile = new File(filePath);
        Page<TaskFileVO> taskFileVOPage = new Page<>();
        if (!rootFile.isDirectory()) {
            return taskFileVOPage;
        }
        List<TaskFileVO> taskFileList = new ArrayList<>();
        File[] files = rootFile.listFiles();
        for (File childFile : files) {
            TaskFileVO taskFileVO = new TaskFileVO();
            taskFileVO.setFileName(childFile.getName());
            try {
                taskFileVO.setFileNum(FilteredFileLister.countXmlFiles(childFile.getAbsolutePath())).setFilePath(childFile.getAbsolutePath());
            } catch (IOException e) {
                taskFileVO.setFileNum(0L);
            }
            taskFileList.add(taskFileVO);
        }
        taskFileVOPage = ListPagingUtil.pageList(taskFileList, pageNo, pageSize);
        return taskFileVOPage;
    }

    /**
     * 任务跳转复核
     *
     * @param taskId
     * @return 复核明细
     */
    @Override
    public TaskReviewDetailVO gotoReview(Long taskId) {
        //跳转的文件夹
        List<Long> inspectionIdList = inspectionDataMapper.getInspectionIdAscByTaskId(taskId);
        if (CollUtil.isEmpty(inspectionIdList)) {
            return null;
        }
        InspectionData inspectionData = inspectionDataMapper.selectById(inspectionIdList.get(0));
        TaskReviewDetailVO taskReviewDetailVO = new TaskReviewDetailVO();
        taskReviewDetailVO.setInspectionIdList(inspectionIdList);
        //文件夹缺陷描述
        taskReviewDetailVO.setTaskId(taskId).setLocomotiveNumber(inspectionData.getLocomotiveNumber()).setPantographStatus(inspectionData.getPantographStatus());
        TaskReviewDetailVO.DefectCountVO reviewCountVO = inspectionReviewResultMapper.reviewPicNumCount(taskId);
        //缺陷类型统计
        List<TaskReviewDetailVO.DefectCountVO> defectTypeCountList = inspectionReviewResultMapper.taskDefectTypeCount(taskId);
        defectTypeCountList.add(0, reviewCountVO);
        taskReviewDetailVO.setDefectCountList(defectTypeCountList);
        //查询出缺陷图片
        List<ReviewPictureVO> reviewPictureList = inspectionReviewResultMapper.getCheckPictureInfoList(inspectionIdList.get(0));
        if (CollUtil.isEmpty(reviewPictureList)) {
            return taskReviewDetailVO;
        }
        for (ReviewPictureVO reviewPictureVO : reviewPictureList) {
            reviewPictureVO.setImagePath(CharSequenceUtil.isBlank(reviewPictureVO.getImagePath()) ? "" : inspectHost + reviewPictureVO.getImagePath());
            reviewPictureVO.setConfirmStatusName(ConfirmStatusEnum.getNameByCode(reviewPictureVO.getConfirmStatus()));
            reviewPictureVO.setMarkedRegionList(JSONUtil.toList(reviewPictureVO.getMarkedRegions(), DefectMarkBO.class));
        }
        taskReviewDetailVO.setReviewPictureList(reviewPictureList);
        return taskReviewDetailVO;
    }

    @Override
    public TaskReviewNextFolderVO reviewNextFolder(Long inspectionId) {
        TaskReviewNextFolderVO taskReviewDetailVO = new TaskReviewNextFolderVO();
        try {
            InspectionData inspectionData = inspectionDataMapper.selectById(inspectionId);
            if (null == inspectionData) {
                log.warn("reviewNextFolder: 巡检数据不存在, inspectionId={}", inspectionId);
                return taskReviewDetailVO;
            }
            //文件夹缺陷描述
            String folderProblemDesc = inspectionReviewResultMapper.getProblemDescForFolder(inspectionId);
            taskReviewDetailVO.setLocomotiveNumber(inspectionData.getLocomotiveNumber())
                    .setPantographStatus(PantographStatusEnum.getNameByCode(inspectionData.getPantographStatus()))
                    .setProblemDesc(folderProblemDesc);

            //查询出缺陷图片
            List<ReviewPictureVO> reviewPictureList = new ArrayList<>();
            Task task = super.getById(inspectionData.getTaskId());
            int checkCount = inspectionReviewResultMapper.countByTaskId(task.getId());
            if (TaskStatusEnum.WAIT_CHECK.getCode().equals(task.getTaskStatus()) || checkCount == 0) {
                File folder = new File(inspectDataPath + inspectionData.getLocomotiveImage());
                if (folder.exists()) {
                    File[] imageFiles = folder.listFiles();
                    if (ArrayUtil.isEmpty(imageFiles)) {
                        taskReviewDetailVO.setReviewPictureList(reviewPictureList);
                        return taskReviewDetailVO;
                    }
                    ReviewPictureVO dbRp = inspectionReviewResultMapper.getUnCheckPictureInfo(inspectionId);
                    for (File imageFile : imageFiles) {
                        if (imageFile.isFile() && FileUtil.judgeImageFile(imageFile)) {
                            ReviewPictureVO reviewPictureVO = new ReviewPictureVO();
                            BeanUtils.copyProperties(dbRp, reviewPictureVO);
                            reviewPictureVO.setImagePath("/image" + imageFile.getAbsolutePath().replace(inspectDataPath, ""));
                            reviewPictureVO.setNetPath(reviewPictureVO.getImagePath());
                            reviewPictureVO.setConfirmStatus(ConfirmStatusEnum.NO_VIEW.getCode());
                            reviewPictureVO.setConfirmStatusName(ConfirmStatusEnum.NO_VIEW.getName());
                            reviewPictureList.add(reviewPictureVO);
                        }
                    }
                }
            } else {
                reviewPictureList = inspectionReviewResultMapper.getCheckPictureInfoList(inspectionId);
                if (CollUtil.isEmpty(reviewPictureList)) {
                    return taskReviewDetailVO;
                }
                for (ReviewPictureVO reviewPictureVO : reviewPictureList) {
                    reviewPictureVO.setImagePath(buildImageUrl(reviewPictureVO.getNetPath()));
                    reviewPictureVO.setConfirmStatus(null==reviewPictureVO.getConfirmStatus() ? ConfirmStatusEnum.NO_VIEW.getCode() : reviewPictureVO.getConfirmStatus());
                    reviewPictureVO.setConfirmStatusName(ConfirmStatusEnum.getNameByCode(reviewPictureVO.getConfirmStatus()));
                    reviewPictureVO.setMarkedRegionList(parseMarkedRegions(reviewPictureVO.getMarkedRegions()));
                }
            }
            taskReviewDetailVO.setReviewPictureList(reviewPictureList);
        } catch (Exception e) {
            log.error("reviewNextFolder_error, inspectionId={}: {}", inspectionId, e.getLocalizedMessage(), e);
        }
        return taskReviewDetailVO;
    }

    /**
     * 构造图片访问 URL：netPath 为空返回空串；未带配置的 host 前缀时补上，避免重复拼接。
     */
    private String buildImageUrl(String netPath) {
        if (CharSequenceUtil.isBlank(netPath)) {
            return "";
        }
        if (netPath.startsWith(inspectHost)) {
            return netPath;
        }
        return inspectHost + netPath;
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

    @Override
    public List<TaskPullDownVO> taskPulldownList() {
        List<TaskPullDownVO> taskPullDownVOS = super.baseMapper.taskPulldownList();
        if (CollUtil.isEmpty(taskPullDownVOS)) {
            return new ArrayList<>();
        }
        for (TaskPullDownVO taskPullDownVO : taskPullDownVOS) {
            taskPullDownVO.setTaskStatusName(TaskStatusEnum.getNameByCode(taskPullDownVO.getTaskStatus()));
        }
        return taskPullDownVOS;
    }

    @Override
    public Boolean pauseAnalysis(Long taskId) {
        TASK_WAIT_QUEUE.remove(taskId);
        TASK_MONITOR_MAP.put(taskId + "_status", "pause");
        Task task = super.getById(taskId);
        LocalDateTime pauseTime = LocalDateTime.now();
        LocalDateTime previousPauseTime = task.getPauseTime();
        long existingDuration = null != task.getAnalyzedDuration() ? task.getAnalyzedDuration() : 0L;
        long durationSeconds;
        if (null != previousPauseTime) {
            durationSeconds = existingDuration + Duration.between(previousPauseTime, pauseTime).getSeconds();
        } else if (null != task.getStartCheckTime()) {
            durationSeconds = Duration.between(task.getStartCheckTime(), pauseTime).getSeconds();
        } else {
            durationSeconds = existingDuration;
        }
        LambdaUpdateWrapper<Task> pauseUpdate = new LambdaUpdateWrapper<Task>()
                .eq(Task::getId, taskId)
                .set(Task::getTaskStatus, Task.TASK_STATUS_PAUSE)
                .set(Task::getPauseTime, pauseTime)
                .set(Task::getAnalyzedDuration, durationSeconds);
        super.update(pauseUpdate);
        return true;
    }

    /**
     * 机车图片缺陷确认
     * <p>
     * 处理流程：
     * 1. 保存/更新复核结果（标记区域追加合并）
     * 2. 更新巡检数据的分析状态、确认状态、受电弓状态
     * 3. 确认缺陷且受电弓异常时，自动入库至缺陷库
     * 4. 更新任务的复核数量统计
     *
     * @param confirmDefectBO 缺陷确认参数
     * @return 操作是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmDefect(ConfirmDefectBO confirmDefectBO) throws ServiceException {
        try {
            // === 1. 保存/更新复核结果 ===
            InspectionData inspectionData = null;
            Task task = null;
            if (CollUtil.isNotEmpty(confirmDefectBO.getMarkedRegions())) {
                confirmDefectBO.setPantographStatus("异常");
            } else {
                confirmDefectBO.setPantographStatus("正常");
            }
            InspectionReviewResult reviewResult = inspectionReviewResultMapper.selectById(confirmDefectBO.getInspectionResultId());

            if (InspectionData.PANTOGRAPH_STATUS_RIGHT.equals(reviewResult.getPantographStatus()) && CollUtil.isEmpty(confirmDefectBO.getMarkedRegions())) {
                throw new ServiceException("请正常确认再保存");
            }

            // 去除图片路径中的host前缀，保留相对路径
            String relativeImagePath = confirmDefectBO.getLocomotiveImage().replace(inspectHost, "");
            Integer pantographStatus = "正常".equals(confirmDefectBO.getPantographStatus())?PantographStatusEnum.NORMAL.getCode():PantographStatusEnum.ERROR.getCode();
            reviewResult.setInspectionId(reviewResult.getInspectionId())
                    .setImagePath(relativeImagePath)
                    .setPantographStatus(pantographStatus)
                    .setProblemDesc(confirmDefectBO.getProblemDesc())
                    .setLocomotiveNumber(confirmDefectBO.getLocomotiveNumber())
                    .setConfirmStatus(confirmDefectBO.getConfirmStatus());

            //如果受电工状态异常
//            List<DefectMarkBO> markList = new ArrayList<>();
            if (CollUtil.isNotEmpty(confirmDefectBO.getMarkedRegions())) {
                // 合并标记区域：已有标记 + 本次新增标记
//                markList = mergeMarkedRegions(reviewResult.getMarkedRegions(), confirmDefectBO.getMarkedRegions());
                reviewResult.setMarkedRegions(JSONUtil.toJsonStr(confirmDefectBO.getMarkedRegions()));
            }
            if (InspectionData.PANTOGRAPH_STATUS_RIGHT.equals(reviewResult.getPantographStatus())) {
                reviewResult.setMarkedRegions("[]");
            }

            inspectionReviewResultMapper.updateById(reviewResult);

            // === 2. 更新巡检数据状态 ===
            inspectionData = inspectionDataMapper.selectById(reviewResult.getInspectionId());
            inspectionData.setAnalysisStatus(AnalyzeStatusEnum.REVIEWED.getCode());

            // 确认状态：确认操作要检查所有InspectionReviewResult的source为1的数据都已确认，其它数据是否都已查看，如果满足条件则更新巡检数据的为已确认状态；
            //如果InspectionReviewResult的source为1的数据还有没确认的，其它数据都已查看，那么巡检数据置为已查看状态；
            if (ConfirmStatusEnum.CONFIRMED.getCode().equals(confirmDefectBO.getConfirmStatus())) {
                if (isAllAiConfirmedAndOthersViewed(inspectionData.getId())) {
                    inspectionData.setConfirmStatus(ConfirmStatusEnum.CONFIRMED.getCode());
                } else if (isAllOthersViewed(inspectionData.getId())) {
                    inspectionData.setConfirmStatus(ConfirmStatusEnum.VIEWED.getCode());
                }
            } else {
                Long unviewCount = inspectionReviewResultMapper.countUnViewPicture(inspectionData.getId());
                inspectionData.setConfirmStatus(unviewCount == 0
                        ? ConfirmStatusEnum.VIEWED.getCode()
                        : ConfirmStatusEnum.NO_VIEW.getCode());
            }

            // 受电弓状态：该巡检下存在异常记录则为异常，否则正常
            pantographStatus = inspectionReviewResultMapper.countErrorStatusByInspectionId(inspectionData.getId()) > 0
                    ? InspectionData.PANTOGRAPH_STATUS_ERROR
                    : InspectionData.PANTOGRAPH_STATUS_RIGHT;
            inspectionData.setPantographStatus(pantographStatus);
            inspectionDataMapper.updateById(inspectionData);

            // === 4. 更新任务复核数量 ===
            super.baseMapper.updateTaskReviewNums(inspectionData.getTaskId());


            // === 3. 确认缺陷且受电弓异常 → 入库缺陷库 ===

            if (CollUtil.isEmpty(confirmDefectBO.getMarkedRegions())) {
                String imagePath = defectStorageMapper.getImagePathByInspectionId(confirmDefectBO.getInspectionResultId());
                File file = new File(inspectDataPath + imagePath);
                if (file.exists()) {
                    file.delete();
                }
                defectStorageMapper.deleteByInspectionId(confirmDefectBO.getInspectionResultId());
            }
            if (isConfirmedDefect(confirmDefectBO)) {
                task = super.getById(inspectionData.getTaskId());
                saveDefectStorage(inspectionData, task, reviewResult, confirmDefectBO.getMarkedRegions(), confirmDefectBO.getInspectionResultId(), LogicStatusEnum.NO.getCode());
            }
        } catch (Exception e1) {
            log.error("确认缺陷异常：{}", e1.getLocalizedMessage(), e1);
            throw new ServiceException(ResultCode.REVIEW_FAIL.getCode(), e1.getMessage());
        }
        return true;
    }

    /**
     * 解码复核图片路径。文件名中的 + 原样保留：
     * URLDecoder 的 x-www-form-urlencoded 语义会把 + 当作空格，导致含 + 的目录（如 K580+561）定位失败。
     */
    private String decodeImagePath(String imagePath) {
        if (CharSequenceUtil.isBlank(imagePath)) {
            return imagePath;
        }
        try {
            return URLUtil.decode(imagePath.replace("+", "%2B"), "UTF-8");
        } catch (Exception e) {
            log.warn("复核图片路径解码失败，按原路径处理: {}", imagePath);
            return imagePath;
        }
    }

    /**
     * 取路径最后一段（目录名或文件名），路径为空时返回 null。
     */
    private String lastPathSegment(String path) {
        if (CharSequenceUtil.isBlank(path)) {
            return null;
        }
        String trimmed = CharSequenceUtil.removeSuffix(path, "/");
        return trimmed.substring(trimmed.lastIndexOf("/") + 1);
    }

    /**
     * 合并标记区域：将已有标记区域与新增标记区域合并，并移除两个集合中的交集
     */
    private List<DefectMarkBO> mergeMarkedRegions(String existingRegions, List<DefectMarkBO> newRegions) {
        Set<DefectMarkBO> existingSet = new LinkedHashSet<>(CharSequenceUtil.isNotBlank(existingRegions)
                ? JSONUtil.toList(existingRegions, DefectMarkBO.class)
                : new ArrayList<>());
        Set<DefectMarkBO> newSet = new LinkedHashSet<>(CollUtil.isEmpty(newRegions)
                ? new ArrayList<>()
                : newRegions);
        Set<DefectMarkBO> intersection = new LinkedHashSet<>(existingSet);
        intersection.retainAll(newSet);
        existingSet.removeAll(intersection);
        newSet.removeAll(intersection);
        List<DefectMarkBO> merged = new ArrayList<>(existingSet.size() + newSet.size());
        merged.addAll(existingSet);
        merged.addAll(newSet);
        return merged;
    }

    /**
     * 判断是否为确认的缺陷：确认状态=已确认 且 存在标记区域（即受电弓异常）
     */
    private boolean isConfirmedDefect(ConfirmDefectBO confirmDefectBO) {
        return ConfirmStatusEnum.CONFIRMED.getCode().equals(confirmDefectBO.getConfirmStatus())
                && CollUtil.isNotEmpty(confirmDefectBO.getMarkedRegions());
    }

    /**
     * 将确认的缺陷保存至缺陷库
     * <p>
     * 若 inspectionData 或 task 为空（例如数据已被清理），则从同 inspectionResultId 的历史缺陷库记录
     * （originalDs）中补全字段，确保入库不丢失上下文。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDefectStorage(InspectionData inspectionData, Task task,
                                  InspectionReviewResult reviewResult, List<DefectMarkBO> markList, Long inspectionResultId, Integer misreport) throws ServiceException {
        // 字段缓存：仅当存在缺失来源时再查询历史记录，避免无谓 DB 访问
        DefectStorage originalDs = null;
        if (null == inspectionData || null == task) {
            originalDs = defectStorageMapper.getOneRecordByInspectionResultId(inspectionResultId);
        }

        Long taskId = null != inspectionData ? inspectionData.getTaskId()
                : (null != originalDs ? originalDs.getTaskId() : null);
        String taskName = null != task ? task.getTaskName()
                : (null != originalDs ? originalDs.getTaskName() : null);
        LocalDateTime inspectionTime = null != inspectionData ? inspectionData.getInspectionTime()
                : (null != originalDs ? originalDs.getInspectionTime() : null);
        Long lineId = null != inspectionData ? inspectionData.getLineId()
                : (null != originalDs ? originalDs.getLineId() : null);
        Long stationId = null != inspectionData ? inspectionData.getStationId()
                : (null != originalDs ? originalDs.getStationId() : null);
        Long deviceId = null != inspectionData ? inspectionData.getDeviceId() : null;
        String locomotiveNumber = null != inspectionData ? inspectionData.getLocomotiveNumber()
                : (null != originalDs ? originalDs.getLocomotiveNumber() : null);

        Line line = null != lineId ? lineMapper.selectById(lineId) : null;
        Station station = null != stationId ? stationMapper.selectById(stationId) : null;
        Device device = null != deviceId ? deviceMapper.selectById(deviceId) : null;

        String poleNo = null != device ? device.getPillarNo()
                : (null != originalDs ? originalDs.getPoleNo() : null);
        String kmSign = null != device ? device.getKmSign()
                : (null != originalDs ? originalDs.getKmSign() : null);
        String lineName = null != line ? line.getLineName()
                : (null != originalDs ? originalDs.getLineName() : null);
        String stationName = null != station ? station.getStationName()
                : (null != originalDs ? originalDs.getStationName() : null);
        List<String> coordList = markList.stream().map(DefectMarkBO::getCoord).collect(Collectors.toList());
        List<String> dbCoordList = defectStorageMapper.getImageAllCoord(reviewResult.getId());
        if (coordList.size() <= dbCoordList.size()) {
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "这些缺陷已入库");
        }
        long inspectionId = null != inspectionData && null != inspectionData.getId() ? inspectionData.getId()
                : (null != reviewResult && null != reviewResult.getInspectionId() ? reviewResult.getInspectionId() : 0L);
        // 巡检数据缺失时，用复核结果中的图片路径推导数据目录名
        String folderName = null != inspectionData
                ? lastPathSegment(inspectionData.getLocomotiveImage())
                : lastPathSegment(decodeImagePath(reviewResult.getImagePath()));
        String folderPath = defectpath + "/" + (taskName == null ? "living" : taskName) + "/" + inspectionId;
        if (CharSequenceUtil.isNotBlank(folderName)) {
            folderPath += "/" + folderName;
        }
        File taskFolder = new File(folderPath);
        if (!taskFolder.exists()) {
            taskFolder.mkdirs();
        }
        for (DefectMarkBO defectMarkBO : markList) {
            DefectStorage defectStorage = defectStorageMapper.getOneRecord(defectMarkBO.getCoord(), reviewResult.getId());
            if (null != defectStorage) {
                continue;
            }
            defectStorage = new DefectStorage();
            String imagePath = inspectDataPath + "/" + decodeImagePath(reviewResult.getImagePath());
            File imagefile = new File(imagePath);
            File defectFile = new File(taskFolder, imagefile.getName());
            try {
                if (!defectFile.exists()) {
                    Files.copy(imagefile.toPath(), defectFile.toPath());
                }
            } catch (IOException e) {
                log.error("缺陷图片复制失败, source={}, target={}", imagefile.getAbsolutePath(), defectFile.getAbsolutePath(), e);
                throw new ServiceException(ResultCode.FILE_UPLOAD_FAILED.getCode(), "缺陷图片写入缺陷仓库失败");
            }

            defectStorage.setReviewStatus(DefectReviewStatusEnum.NO_REVIEW.getCode())
                    .setDefectLevel(1)
                    .setTaskId(taskId)
                    .setInspectionResultId(reviewResult.getId())
                    .setTaskName(taskName)
                    .setDefectType(defectMarkBO.getDefectType())
                    .setInspectionTime(inspectionTime)
                    .setLineId(lineId)
                    .setPoleNo(poleNo)
                    .setKmSign(kmSign)
                    .setLocomotiveNumber(locomotiveNumber)
                    .setReviewerId(CurrentUserHolder.get().getId())
                    .setReviewer(CurrentUserHolder.get().getUserName() + "(" + CurrentUserHolder.get().getAccountNo() + ")")
                    .setLineName(lineName)
                    .setStationId(stationId)
                    .setStationName(stationName)
                    .setNetPath(taskFolder.getAbsolutePath().replace(defectpath, inspectDefect) + "/" + imagefile.getName())
                    .setImagePath(taskFolder.getAbsolutePath().replace(defectpath, "") + "/" + imagefile.getName())
                    .setMarkedRegions(defectMarkBO.getCoord())
                    .setSource(defectMarkBO.getSource())
                    .setMisreport(misreport);
            defectStorageMapper.insert(defectStorage);
        }
    }

    @Override
    public TaskReviewDetailVO checkFolderSearch(TaskReviewQO taskReviewQO) {
        //跳转的文件夹
        List<Long> inspectionIdList = inspectionDataMapper.getInspectionIdAscByCont(taskReviewQO);
        if (CollUtil.isEmpty(inspectionIdList)) {
            return null;
        }
        InspectionData inspectionData = inspectionDataMapper.selectById(inspectionIdList.get(0));
        TaskReviewDetailVO taskReviewDetailVO = new TaskReviewDetailVO();
        taskReviewDetailVO.setInspectionIdList(inspectionIdList);
        taskReviewDetailVO.setTaskId(taskReviewQO.getTaskId()).setLocomotiveNumber(inspectionData.getLocomotiveNumber()).setPantographStatus(inspectionData.getPantographStatus());
        TaskReviewDetailVO.DefectCountVO reviewCountVO = inspectionReviewResultMapper.reviewPicNumCount(taskReviewQO.getTaskId());
        //缺陷类型统计
        List<TaskReviewDetailVO.DefectCountVO> defectTypeCountList = inspectionReviewResultMapper.taskDefectTypeCount(taskReviewQO.getTaskId());
        defectTypeCountList.add(0, reviewCountVO);
        taskReviewDetailVO.setDefectCountList(defectTypeCountList);
        List<ReviewPictureVO> reviewPictureList = new ArrayList<>();
        Task task = super.getById(inspectionData.getTaskId());
        int checkCount = inspectionReviewResultMapper.countByTaskId(task.getId());
        if (TaskStatusEnum.WAIT_CHECK.getCode().equals(task.getTaskStatus()) || checkCount == 0) {
            File folder = new File(inspectDataPath + inspectionData.getLocomotiveImage());
            if (folder.exists()) {
                File[] imageFiles = folder.listFiles();
                if (ArrayUtil.isEmpty(imageFiles)) {
                    taskReviewDetailVO.setReviewPictureList(reviewPictureList);
                    return taskReviewDetailVO;
                }
                ReviewPictureVO dbRp = inspectionReviewResultMapper.getUnCheckPictureInfo(inspectionIdList.get(0));
                for (File imageFile : imageFiles) {
                    if (imageFile.isFile() && FileUtil.judgeImageFile(imageFile)) {
                        ReviewPictureVO reviewPictureVO = new ReviewPictureVO();
                        BeanUtils.copyProperties(dbRp, reviewPictureVO);
                        reviewPictureVO.setImagePath("/image" + imageFile.getAbsolutePath().replace(inspectDataPath, ""));
                        reviewPictureVO.setNetPath(reviewPictureVO.getImagePath());
                        reviewPictureVO.setConfirmStatus(ConfirmStatusEnum.NO_VIEW.getCode());
                        reviewPictureVO.setConfirmStatusName(ConfirmStatusEnum.NO_VIEW.getName());
                        reviewPictureList.add(reviewPictureVO);
                    }
                }
            }
        } else {
            //查询出缺陷图片
            reviewPictureList = inspectionReviewResultMapper.getCheckPictureInfoList(inspectionIdList.get(0));
            if (CollUtil.isEmpty(reviewPictureList)) {
                return taskReviewDetailVO;
            }
            for (ReviewPictureVO reviewPictureVO : reviewPictureList) {
                if (!reviewPictureVO.getNetPath().startsWith("/image")) {
                    reviewPictureVO.setNetPath("/image" + reviewPictureVO.getNetPath());
                }
                reviewPictureVO.setImagePath(reviewPictureVO.getNetPath());
                reviewPictureVO.setConfirmStatusName(ConfirmStatusEnum.getNameByCode(reviewPictureVO.getConfirmStatus()));
                reviewPictureVO.setMarkedRegionList(JSONUtil.toList(reviewPictureVO.getMarkedRegions(), DefectMarkBO.class));
            }
        }
        taskReviewDetailVO.setReviewPictureList(reviewPictureList);
        return taskReviewDetailVO;
    }

    @Override
    public List<String> taskDefectTypeList(Long taskId) {
        return inspectionReviewResultMapper.getDefectTypeByTaskId(taskId);
    }

    @Override
    public Boolean deleteById(Long id) throws ServiceException {
        Task task = super.getById(id);
        if (!apiUtils.isAdmin() && !apiUtils.currentAccount().equals(task.getCreatedUser())) {
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "非管理员只能删除自己创建的任务");
        }
        if (TaskStatusEnum.CHECKING.getCode().equals(task.getTaskStatus())) {
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "分析中的任务不能删除！");
        }
        inspectionDataMapper.deleteByTaskId(id);
        inspectionReviewResultMapper.deleteByTaskId(id);
        taskFileMapper.deleteByTaskId(id);
        return super.removeById(id);
    }

    @Override
    public Boolean deleteByIds(List<Long> ids) throws ServiceException {
        List<Task> taskList = super.baseMapper.getAnalyzingTaskList(ids);
        if (CollUtil.isNotEmpty(taskList)) {
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "分析中的任务不能删除！");
        }
        taskList = super.listByIds(ids);
        List<String> createUserList = taskList.stream().map(Task::getCreatedUser).collect(Collectors.toList());
        List<String> otherUser = createUserList.stream().filter(u -> !u.equals(apiUtils.currentAccount())).collect(Collectors.toList());
        if (!apiUtils.isAdmin() && CollUtil.isNotEmpty(otherUser)) {
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "非管理员只能删除自己创建的任务");
        }
        inspectionDataMapper.deleteByTaskIds(ids);
        inspectionReviewResultMapper.deleteByTaskIds(ids);
        taskFileMapper.deleteByTaskIds(ids);
        return super.removeByIds(ids);
    }

    @Override
    public Page<InspectionDataVO> taskFolderPaging(TaskReviewQO taskReviewQO) {
        //跳转的文件夹
        Integer offset = (taskReviewQO.getPageNo() - 1) * taskReviewQO.getPageSize();
        taskReviewQO.setOffset(offset);
        List<Long> inspectionIdList = inspectionDataMapper.getInspectionIdAscPaging(taskReviewQO);
        Long total = inspectionDataMapper.countInspectionIdAscPaging(taskReviewQO);
        Page<InspectionDataVO> page = new Page<>(taskReviewQO.getPageNo(), taskReviewQO.getPageSize(), total);
        if (CollUtil.isEmpty(inspectionIdList)) {
            return page;
        }
        List<InspectionData> inspectionDataList = inspectionDataMapper.selectBatchIds(inspectionIdList);
        if (CollUtil.isEmpty(inspectionDataList)) {
            return page;
        }
        List<InspectionDataVO> inspectionDataVOList = new ArrayList<>();
        inspectionDataList.forEach(inspectionData -> {
            InspectionDataVO inspectionDataVO = new InspectionDataVO();
            BeanUtils.copyProperties(inspectionData, inspectionDataVO);
            Station station = stationMapper.selectById(inspectionData.getStationId());
            Device device = deviceMapper.selectById(inspectionData.getDeviceId());
            inspectionDataVO.setStationName(null == station ? "" : station.getStationName());
            inspectionDataVO.setPoleNo(null == device ? "" : device.getPillarNo());
            inspectionDataVO.setConfirmStatus(null==inspectionDataVO.getConfirmStatus()?ConfirmStatusEnum.NO_VIEW.getCode():inspectionDataVO.getConfirmStatus());
            inspectionDataVO.setConfirmStatusName(ConfirmStatusEnum.getNameByCode(inspectionDataVO.getConfirmStatus()));
            inspectionDataVOList.add(inspectionDataVO);
        });
        inspectionDataVOList.sort(Comparator.comparing(InspectionDataVO::getInspectionTime));
        page.setRecords(inspectionDataVOList);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDefect(DefectDeleteBO defectDefectBO) throws ServiceException {
        try {
            if(DefectSourceTypeEnum.AI.getCode().equals(defectDefectBO.getMarkedRegion().getSource())){
                throw new ServiceException("算法分析出的缺陷不能删除");
            }
            InspectionReviewResult reviewResult = inspectionReviewResultMapper.selectById(defectDefectBO.getInspectionResultId());
            List<DefectMarkBO> defectMarkBOList = JSONUtil.toList(reviewResult.getMarkedRegions(), DefectMarkBO.class);
            defectMarkBOList.remove(defectDefectBO.getMarkedRegion());
            if (CollUtil.isEmpty(defectMarkBOList)) {
                reviewResult.setPantographStatus(InspectionData.PANTOGRAPH_STATUS_RIGHT);
            }
            reviewResult.setMarkedRegions(JSONUtil.toJsonStr(defectMarkBOList));
            inspectionReviewResultMapper.updateById(reviewResult);
            long errorCount = inspectionReviewResultMapper.judgeFolderSdgStatus(reviewResult.getInspectionId());
            if (errorCount == 0) {
                inspectionDataMapper.updateSdgStatusById(reviewResult.getInspectionId(), InspectionData.PANTOGRAPH_STATUS_RIGHT);
            }
            DefectStorage defectStorage = defectStorageMapper.getOneRecord(defectDefectBO.getMarkedRegion().getCoord(), reviewResult.getId());
            if (null != defectStorage) {
                defectStorageMapper.deleteById(defectStorage);
            }
        } catch (Exception e) {
            log.error("删除缺陷失败_deleteDefect:", e);
            throw new ServiceException(ResultCode.REMOVE_FAIL.getCode(), "删除缺陷失败");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteDefect(Long inspectionResultId) throws ServiceException {
        try {
            InspectionReviewResult reviewResult = inspectionReviewResultMapper.selectById(inspectionResultId);
            //只清除非算法来源的缺陷，保留算法分析出的缺陷
            List<DefectMarkBO> defectMarkBOList = CharSequenceUtil.isNotBlank(reviewResult.getMarkedRegions())
                    ? JSONUtil.toList(reviewResult.getMarkedRegions(), DefectMarkBO.class)
                    : new ArrayList<>();
            defectMarkBOList.removeIf(dm -> !DefectSourceTypeEnum.AI.getCode().equals(dm.getSource()));
            reviewResult.setMarkedRegions(CollUtil.isEmpty(defectMarkBOList) ? "[]" : JSONUtil.toJsonStr(defectMarkBOList));
            if (CollUtil.isEmpty(defectMarkBOList)) {
                reviewResult.setPantographStatus(InspectionData.PANTOGRAPH_STATUS_RIGHT);
            }
            inspectionReviewResultMapper.updateById(reviewResult);
            long errorCount = inspectionReviewResultMapper.judgeFolderSdgStatus(reviewResult.getInspectionId());
            if (errorCount == 0) {
                inspectionDataMapper.updateSdgStatusById(reviewResult.getInspectionId(), InspectionData.PANTOGRAPH_STATUS_RIGHT);
            }
        } catch (Exception e) {
            log.error("批量删除缺陷失败_batchDeleteDefect:", e);
            throw new ServiceException(ResultCode.REMOVE_FAIL.getCode(), "批量删除缺陷失败");
        }
        return true;
    }

    @Override
    public Boolean addDefect(ConfirmDefectBO confirmDefectBO) {
        // === 1. 保存/更新复核结果 ===
        InspectionData inspectionData = null;
        InspectionReviewResult reviewResult = inspectionReviewResultMapper.selectById(confirmDefectBO.getInspectionResultId());
        if (null != reviewResult) {
            confirmDefectBO.getMarkedRegions().forEach(markedRegion -> markedRegion.setSource(DefectSourceTypeEnum.MANUAL.getCode()));
            // 去除图片路径中的host前缀，保留相对路径
            String relativeImagePath = confirmDefectBO.getLocomotiveImage().replace(inspectHost, "");

            reviewResult.setInspectionId(confirmDefectBO.getInspectionId())
                    .setImagePath(relativeImagePath)
                    .setPantographStatus(Integer.valueOf(confirmDefectBO.getPantographStatus()))
                    .setProblemDesc(confirmDefectBO.getProblemDesc())
                    .setLocomotiveNumber(confirmDefectBO.getLocomotiveNumber())
                    .setConfirmStatus(ConfirmStatusEnum.CONFIRMED.getCode());

            // 合并标记区域：已有标记 + 本次新增标记
            List<DefectMarkBO> markList = mergeMarkedRegions(reviewResult.getMarkedRegions(), confirmDefectBO.getMarkedRegions());

            reviewResult.setMarkedRegions(JSONUtil.toJsonStr(markList));

            inspectionReviewResultMapper.updateById(reviewResult);

            // === 2. 更新巡检数据状态 ===
            inspectionData = inspectionDataMapper.selectById(confirmDefectBO.getInspectionId());

            inspectionData.setPantographStatus(InspectionData.PANTOGRAPH_STATUS_ERROR);
            if (isAllAiConfirmedAndOthersViewed(inspectionData.getId())) {
                //算法来源记录已全部确认且其余记录均已查看，巡检数据直接置为已确认
                inspectionData.setConfirmStatus(ConfirmStatusEnum.CONFIRMED.getCode());
            } else {
                // 确认状态：确认操作直接设为已确认；否则根据未查看图片数判断是已查看还是未查看
                Long unviewCount = inspectionReviewResultMapper.countUnViewPicture(inspectionData.getId());
                inspectionData.setConfirmStatus(unviewCount == 0
                        ? ConfirmStatusEnum.VIEWED.getCode()
                        : ConfirmStatusEnum.NO_VIEW.getCode());
            }

            inspectionDataMapper.updateById(inspectionData);
        }
        return true;
    }

    @Override
    public Boolean viewDefect(ViewDefectBO viewDefectBO) {
        InspectionData inspectionData = null;
        if (null == viewDefectBO.getInspectionResultId()) {
            return false;
        }
        InspectionReviewResult reviewResult = inspectionReviewResultMapper.selectById(viewDefectBO.getInspectionResultId());
        //审核结果到已查看
        reviewResult.setConfirmStatus(ConfirmStatusEnum.VIEWED.getCode());

        inspectionReviewResultMapper.updateById(reviewResult);

        // === 1. 更新巡检数据状态 ===
        inspectionData = inspectionDataMapper.selectById(reviewResult.getInspectionId());
        inspectionData.setAnalysisStatus(AnalyzeStatusEnum.REVIEWED.getCode());

        if (isAllAiConfirmedAndOthersViewed(inspectionData.getId())) {
            //算法来源记录已全部确认且其余记录均已查看，巡检数据直接置为已确认
            inspectionData.setConfirmStatus(ConfirmStatusEnum.CONFIRMED.getCode());
        } else {
            Long unviewCount = inspectionReviewResultMapper.countUnViewPicture(inspectionData.getId());
            inspectionData.setConfirmStatus(unviewCount == 0
                    ? ConfirmStatusEnum.VIEWED.getCode()
                    : ConfirmStatusEnum.NO_VIEW.getCode());
        }

        inspectionDataMapper.updateById(inspectionData);

        // === 2. 更新任务复核数量 ===
        super.baseMapper.updateTaskReviewNums(inspectionData.getTaskId());
        return true;
    }

    /**
     * 判断巡检数据下：有算法来源(source=1)的记录时，算法来源记录的 confirm_status 全部为已确认(2)；
     * 如果没有算法来源的记录，只要有一张图片是已确认(2)，
     * 且其它来源记录的 confirm_status 均为已查看(1)或已确认(2)，那么该巡检数据即可置为已确认
     */
    @Override
    public boolean isAllAiConfirmedAndOthersViewed(Long inspectionId) {
        LambdaQueryWrapper<InspectionReviewResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionReviewResult::getInspectionId, inspectionId);
        List<InspectionReviewResult> reviewResults = inspectionReviewResultMapper.selectList(wrapper);
        if (CollUtil.isEmpty(reviewResults)) {
            return false;
        }
        boolean existsAi = false;
        boolean existsConfirmed = false;
        for (InspectionReviewResult result : reviewResults) {
            List<DefectMarkBO> markedRegions = parseMarkedRegions(result.getMarkedRegions());
            boolean aiSource = markedRegions.stream()
                    .anyMatch(dm -> DefectSourceTypeEnum.AI.getCode().equals(dm.getSource()));
            Integer confirmStatus = result.getConfirmStatus();
            if (aiSource) {
                existsAi = true;
                if (!ConfirmStatusEnum.CONFIRMED.getCode().equals(confirmStatus) && !ConfirmStatusEnum.MISREPORT.getCode().equals(result.getConfirmStatus())) {
                    return false;
                }
            } else if (ConfirmStatusEnum.CONFIRMED.getCode().equals(confirmStatus)) {
                existsConfirmed = true;
            } else if (!ConfirmStatusEnum.VIEWED.getCode().equals(confirmStatus)) {
                return false;
            }
        }
        //有算法来源记录时要求算法记录全部已确认；没有算法来源记录时只要有一张图片已确认即可
        return existsAi || existsConfirmed;
    }

    /**
     * 判断巡检数据下非算法来源(其它)记录的 confirm_status 是否均为已查看(1)
     */
    @Override
    public boolean isAllOthersViewed(Long inspectionId) {
        LambdaQueryWrapper<InspectionReviewResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionReviewResult::getInspectionId, inspectionId);
        List<InspectionReviewResult> reviewResults = inspectionReviewResultMapper.selectList(wrapper);
        if (CollUtil.isEmpty(reviewResults)) {
            return false;
        }
        boolean existsOther = false;
        for (InspectionReviewResult result : reviewResults) {
            List<DefectMarkBO> markedRegions = parseMarkedRegions(result.getMarkedRegions());
            if (markedRegions.stream().anyMatch(dm -> DefectSourceTypeEnum.AI.getCode().equals(dm.getSource()))) {
                continue;
            }
            existsOther = true;
            if (!ConfirmStatusEnum.VIEWED.getCode().equals(result.getConfirmStatus())) {
                return false;
            }
        }
        return existsOther;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean autoCreate(TaskPushSaveBO info) throws ServiceException {
        log.info("autoCreate_source_param: {}", JSONUtil.toJsonStr(info));
        if (CharSequenceUtil.isBlank(info.getFolderPath())) {
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "没有可分析的文件");
        }
        //这里引入队列，新来的文件夹路径先放进队列，避免阻塞，从队列中取文件夹路径来解析数据
        try {
            inspectionDataService.parseDataNoTask(info.getFolderPath());
            // AI 服务端口未就绪时拒绝启动；放在排队判断之后，避免对排队中的任务做无意义的端口探测
            CheckResult checkResult = PortChecker.checkPort(ip, port, 500);
            if (!checkResult.getIsOpen()) {
                throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "算法服务未开启");
            }
            List<InspectionData> inspectionDataList = inspectionDataMapper.getByFilePath(info.getFolderPath().replace(inspectDataPath, ""));
            for (InspectionData inspectionData : inspectionDataList) {
                inspectionDataService.processFolderValidation(new File(inspectDataPath + inspectionData.getLocomotiveImage()), inspectionData);
            }
            Map<String, Object> taskStatusMap = new HashMap<>();
            taskStatusMap.put("refresh", "Y");
            Map<String, Long> newsCountMap = new HashMap<>();
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
                return true;
            }
            log.info("newsCountMap: {}", newsCountMap);
            taskStatusMap.put("userNewsCount", newsCountMap);
            log.info("webSocketMsg_realtime_data_count: {}", taskStatusMap);
            WebSocketSessionManager.broadcastMsg(WebSocketMsg.msg("REALTIME_DATA_REFRESH", "PYTHON", taskStatusMap));
        } catch (Exception e) {
            log.error("同步解析任务数据异常, info={}", JSONUtil.toJsonStr(info), e);
            throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(), "实时分析失败");
        }
        return true;
    }

    /**
     * 将待解析文件夹按入队顺序放入解析队列，已在队列或解析中的目录不会重复入队
     */
    @Override
    public boolean enqueuePushParseFolder(String folderPath) {
        if (!PUSH_PARSE_PENDING.add(folderPath)) {
            return false;
        }
        PUSH_PARSE_QUEUE.offer(folderPath);
        log.info("解析文件夹已入队: {}, queueSize={}", folderPath, PUSH_PARSE_QUEUE.size());
        return true;
    }

    /**
     * 定时消费推送解析队列：按 5 个线程的并发度取出文件夹路径解析并触发 AI 分析。
     * 在途任务数达到线程池大小时暂停取用，避免队列被一次性抽空堆积在池的阻塞队列里。
     */
    @Override
    public void consumePushParseQueue() {
        if (PUSH_PARSE_QUEUE.isEmpty()) {
            return;
        }
        // AI 服务端口未就绪时本轮不消费，文件夹保留在队列中等待下一轮
        CheckResult checkResult = PortChecker.checkPort(ip, port, 500);
        if (!checkResult.getIsOpen()) {
            log.info("推送解析队列消费跳过: 算法服务未开启");
            return;
        }
        while (pushParseInFlight.get() < PUSH_PARSE_POOL_SIZE) {
            String folderPath = PUSH_PARSE_QUEUE.poll();
            if (null == folderPath) {
                return;
            }
            pushParseInFlight.incrementAndGet();
            try {
                pushParseExecutor.submit(() -> {
                    try {
                        processPushFolder(folderPath);
                    } catch (Exception e) {
                        log.error("推送数据异步解析异常, folderPath={}", folderPath, e);
                    } finally {
                        PUSH_PARSE_PENDING.remove(folderPath);
                        if (pushParseInFlight.decrementAndGet() == 0 && PUSH_PARSE_QUEUE.isEmpty()) {
                            broadcastPushRealtime();
                        }
                    }
                });
            } catch (Exception e) {
                pushParseInFlight.decrementAndGet();
                PUSH_PARSE_PENDING.remove(folderPath);
                log.error("推送数据提交解析线程池失败, folderPath={}", folderPath, e);
            }
        }
    }

    /**
     * 解析推送的文件夹并对其中的巡查数据做 AI 文件夹分析
     */
    private void processPushFolder(String folderPath) throws ServiceException {
        log.info("推送数据开始解析: {}", folderPath);
        synchronized (PUSH_PARSE_LOCK) {
            inspectionDataService.parseDataNoTask(folderPath);
        }
        List<InspectionData> inspectionDataList = inspectionDataMapper.getByFilePath(folderPath.replace(inspectDataPath, ""));
        for (InspectionData inspectionData : inspectionDataList) {
            inspectionDataService.processFolderValidation(new File(inspectDataPath + inspectionData.getLocomotiveImage()), inspectionData);
        }
        log.info("推送数据解析完成: {}, 巡查数据 {} 条", folderPath, inspectionDataList.size());
    }

    /**
     * 推送解析完成后向各用户推送实时数据增量统计
     */
    private void broadcastPushRealtime() {
        try {
            Map<String, Object> taskStatusMap = new HashMap<>();
            taskStatusMap.put("refresh", "Y");
            Map<String, Long> newsCountMap = new HashMap<>();
            for (Map.Entry<String, Long> entry : InspectionDataServiceImpl.userMaxInspectionIdCache.entrySet()) {
                if (null != entry.getValue()) {
                    long newCount = inspectionDataMapper.countGreatThanCurr(entry.getValue());
                    if (newCount == 0) {
                        continue;
                    }
                    newsCountMap.put(entry.getKey(), newCount);
                }
            }
            if (newsCountMap.isEmpty()) {
                return;
            }
            log.info("newsCountMap: {}", newsCountMap);
            taskStatusMap.put("userNewsCount", newsCountMap);
            log.info("webSocketMsg_realtime_data_count: {}", taskStatusMap);
            WebSocketSessionManager.broadcastMsg(WebSocketMsg.msg("REALTIME_DATA_REFRESH", "PYTHON", taskStatusMap));
        } catch (Exception e) {
            log.error("推送实时数据统计异常", e);
        }
    }
}
