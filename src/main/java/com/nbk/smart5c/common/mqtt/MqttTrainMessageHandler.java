package com.nbk.smart5c.common.mqtt;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.nbk.smart5c.common.utils.DeleteCurrentDirectoryUtil;
import com.nbk.smart5c.common.utils.DirectoryCopyUtil;
import com.nbk.smart5c.common.utils.FileUtil;
import com.nbk.smart5c.common.utils.PortChecker;
import com.nbk.smart5c.entity.Task;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.mapper.TaskMapper;
import com.nbk.smart5c.pojo.bo.MqttTrainMessageBO;
import com.nbk.smart5c.pojo.bo.TaskPushSaveBO;
import com.nbk.smart5c.pojo.vo.CheckResult;
import com.nbk.smart5c.properties.MqttProperties;
import com.nbk.smart5c.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 5C 智能分析数据同步：解析 MQTT 车次消息，将 NFS 中对应车次目录拷贝到本地 rawdata 后解析。
 * <p>
 * 消息到达后只做轻量的路径解析与入队；阻塞队列由定时任务每 {@code mqtt.consume-delay} 毫秒消费一个目录，
 * 避免 MQTT 回调线程被文件拷贝与 AI 解析阻塞。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqttTrainMessageHandler {

    private static final DateTimeFormatter TRAIN_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TRAIN_DATE_STR_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Value("${inspect.basepath}")
    private String inspectBasePath;
    @Value("${inspect.mountpath}")
    private String mountPath;
    @Value("${ai.ip}")
    private String aiIp;
    @Value("${ai.port}")
    private Integer aiPort;

    private final MqttProperties mqttProperties;
    private final TaskService<TaskMapper, Task> taskService;

    /**
     * 待同步解析的车次目录（先进先出）
     */
    private LinkedBlockingQueue<SyncTask> syncQueue;

    /**
     * 已在队列中或正在处理的目标目录，用于消息去重
     */
    private final Map<String, Boolean> pendingPaths = new ConcurrentHashMap<>();

    /**
     * 消费互斥标记，保证同一时刻只有一个目录被拷贝/解析
     */
    private final AtomicBoolean consuming = new AtomicBoolean(false);

    /**
     * 已解析成功的目录，24 小时内忽略同一目录的重复消息，避免重复解析产生重复巡查数据
     */
    private final Cache<String, Boolean> processedCache = Caffeine.newBuilder()
            .maximumSize(20000)
            .expireAfterWrite(24, TimeUnit.HOURS)
            .build();

    @PostConstruct
    void initQueue() {
        syncQueue = new LinkedBlockingQueue<>(mqttProperties.getQueueCapacity());
    }

    /**
     * 处理一条 MQTT 车次消息：构建源/目标目录并入队
     */
    public void handleMessage(MqttProperties.Segment segment, String payload) {
        MqttTrainMessageBO message;
        try {
            message = JSONUtil.toBean(payload, MqttTrainMessageBO.class);
        } catch (Exception e) {
            log.error("[mqtt][{}] 消息解析失败: {}", segment.getName(), payload, e);
            return;
        }
        if (null == message || StrUtil.isBlank(message.getSeg()) || StrUtil.isBlank(message.getTrainPath())
                || StrUtil.isBlank(message.getTrainDate())) {
            log.warn("[mqtt][{}] 消息缺少必要字段, payload={}", segment.getName(), payload);
            return;
        }
        if (!segment.getName().equalsIgnoreCase(message.getSeg())) {
            log.warn("[mqtt][{}] 消息 seg={} 与订阅段不一致，按配置段处理", segment.getName(), message.getSeg());
        }

        SyncTask task = buildSyncTask(segment, message);
        if (null == task) {
            return;
        }
        if (null != processedCache.getIfPresent(task.targetDir)) {
            log.info("[mqtt][{}] 车次目录已解析过，忽略重复消息: {}", segment.getName(), task.targetDir);
            return;
        }
        if (pendingPaths.putIfAbsent(task.targetDir, Boolean.TRUE) != null) {
            log.info("[mqtt][{}] 车次目录已在队列或处理中，忽略重复消息: {}", segment.getName(), task.targetDir);
            return;
        }
        if (!syncQueue.offer(task)) {
            pendingPaths.remove(task.targetDir);
            log.error("[mqtt][{}] 同步队列已满，丢弃车次目录: {}", segment.getName(), task.targetDir);
            return;
        }
        log.info("[mqtt][{}] 车次目录已入队: {} -> {}, queueSize={}",
                segment.getName(), task.sourceDir, task.targetDir, syncQueue.size());
    }

    /**
     * 构建同步任务：源目录 ${inspect.mountpath}/{段目录}{trainPath}，
     * 目标目录 ${inspect.basepath}/{trainDateStr}/{seg}/{trainDir}
     */
    private SyncTask buildSyncTask(MqttProperties.Segment segment, MqttTrainMessageBO message) {
        LocalDateTime trainDate;
        try {
            trainDate = LocalDateTime.parse(message.getTrainDate(), TRAIN_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("[mqtt][{}] trainDate 格式无法解析: {}", segment.getName(), message.getTrainDate());
            return null;
        }
        String trainDir = StrUtil.isNotBlank(message.getTrainDir())
                ? message.getTrainDir()
                : new File(message.getTrainPath()).getName();
        // trainDir 直接参与拼路径，拒绝含路径分隔符或 .. 的取值，避免越出 rawdata 目录
        if (StrUtil.isBlank(trainDir) || trainDir.contains("/") || trainDir.contains("\\") || trainDir.contains("..")) {
            log.error("[mqtt][{}] 车次目录名非法: {}", segment.getName(), trainDir);
            return null;
        }

        // trainPath 以 "/" 开头，先去掉再拼接，避免出现 "//" 影响后续按路径截取
        String relativeTrainPath = StrUtil.removePrefix(message.getTrainPath(), "/");
        String sourceDir = new File(new File(mountPath, segment.getSegmentDir()), relativeTrainPath)
                .getAbsolutePath();
        String trainDateStr = trainDate.format(TRAIN_DATE_STR_FORMATTER);
        // 目标目录使用段的映射目录名（jinan/qingdao/weiguan），而非消息中的 seg 值（jn/qd/wg）
        File segmentRoot = new File(new File(inspectBasePath, trainDateStr), segment.getSegmentDir());
        // 按需求在 ${inspect.basepath} 下预建 trainDateStr/段映射目录 层级目录
        if (!segmentRoot.isDirectory() && !segmentRoot.mkdirs() && !segmentRoot.isDirectory()) {
            log.error("[mqtt][{}] 创建段数据目录失败: {}", segment.getName(), segmentRoot.getAbsolutePath());
            return null;
        }
        String targetDir = new File(segmentRoot, trainDir).getAbsolutePath();
        return new SyncTask(sourceDir, targetDir);
    }

    /**
     * 由定时任务调用：从队列取出一个车次目录同步并解析。
     * 失败时仅打印失败路径，不再重新入队；成功则下一轮继续消费下一条。
     */
    public void consumeOne() {
        if (syncQueue.isEmpty() || !consuming.compareAndSet(false, true)) {
            return;
        }
        SyncTask task = null;
        try {
            task = syncQueue.poll();
            if (null == task) {
                return;
            }
            // 算法服务未就绪时本轮不消费，任务留在队列中等待下一轮（不属于处理失败）
            CheckResult checkResult = PortChecker.checkPort(aiIp, aiPort, 500);
            if (!checkResult.getIsOpen()) {
                log.info("[mqtt] 算法服务未开启，暂停消费车次目录");
                syncQueue.offer(task);
                return;
            }
            process(task.targetDir, task.sourceDir);
        } catch (Exception e) {
            log.error("[mqtt] 车次目录同步解析异常: {}", task, e);
            if (null != task) {
                markFailed(task.targetDir, task.sourceDir, "处理异常: " + e.getMessage());
            }
        } finally {
            consuming.set(false);
        }
    }

    private void process(String targetDir, String sourceDir) {
        if (!new File(sourceDir).isDirectory()) {
            markFailed(targetDir, sourceDir, "源目录不存在");
            return;
        }

        // 目标目录已存在（如同一车次重复同步），先清理再重新完整拷贝，避免残留旧数据
        File target = new File(targetDir);
        if (target.exists()) {
            try {
                DeleteCurrentDirectoryUtil.deleteCurrentDirectoryWithWalkFileTree(Paths.get(targetDir));
            } catch (Exception e) {
                markFailed(targetDir, sourceDir, "清理目标目录失败: " + e.getMessage());
                return;
            }
        }
        File parent = target.getParentFile();
        if (null != parent && !parent.exists() && !parent.mkdirs()) {
            markFailed(targetDir, sourceDir, "创建目标父目录失败: " + parent.getAbsolutePath());
            return;
        }
        if (!DirectoryCopyUtil.copyDirectory(sourceDir, targetDir, true)) {
            // 清理半成品目录，避免残留不完整数据
            try {
                DeleteCurrentDirectoryUtil.deleteCurrentDirectoryWithWalkFileTree(Paths.get(targetDir));
            } catch (Exception e) {
                log.warn("[mqtt] 清理拷贝失败目录异常: {}", targetDir, e.getMessage());
            }
            markFailed(targetDir, sourceDir, "拷贝车次目录失败");
            return;
        }

        // 拷贝完成后删除车次目录下文件名以 _md、_sl 结尾的图片
        int deletedCount = FileUtil.deleteMdSlImages(target);

        try {
            TaskPushSaveBO pushParam = new TaskPushSaveBO();
            pushParam.setFolderPath(targetDir);
            if (Boolean.TRUE.equals(taskService.autoCreate(pushParam))) {
                pendingPaths.remove(targetDir);
                processedCache.put(targetDir, Boolean.TRUE);
                log.info("[mqtt] 车次目录解析成功: {}, 删除 _md/_sl 图片 {} 张", targetDir, deletedCount);
                return;
            }
            markFailed(targetDir, sourceDir, "autoCreate 返回失败");
        } catch (ServiceException e) {
            markFailed(targetDir, sourceDir, "autoCreate 异常: " + e.getMessage());
        }
    }

    /**
     * 处理失败：打印失败路径并移除去重标记（不重新入队）
     */
    private void markFailed(String targetDir, String sourceDir, String reason) {
        pendingPaths.remove(targetDir);
        log.error("[mqtt] 车次目录同步解析失败, 不再重试。targetDir={}, sourceDir={}, 原因: {}",
                targetDir, sourceDir, reason);
    }

    /**
     * 队列长度，供监控使用
     */
    public int queueSize() {
        return syncQueue.size();
    }

    /**
     * 一个待同步的车次目录
     */
    private static class SyncTask {
        private final String sourceDir;
        private final String targetDir;

        private SyncTask(String sourceDir, String targetDir) {
            this.sourceDir = sourceDir;
            this.targetDir = targetDir;
        }

        @Override
        public String toString() {
            return sourceDir + " -> " + targetDir;
        }
    }
}
