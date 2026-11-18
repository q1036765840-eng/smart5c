package com.nbk.smart5c.common.utils;

import com.nbk.smart5c.common.websocket.WebSocketMsg;
import com.nbk.smart5c.common.websocket.WebSocketSessionManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 导出进度 WebSocket 推送：cmd=EXPORT_PROGRESS，data={exportType, exportProgress, exportStatus}。
 * progress 为 -1 表示失败，100 表示完成；相同进度不重复推送。
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExportProgressUtil {

    /** 实时数据导出（zip） */
    public static final String EXPORT_TYPE_INSPECTION_DATA = "REALTIME_DATA";
    /** 首页最新报警报告（xlsx） */
    public static final String EXPORT_TYPE_HOME_REPORT = "HOME_REPORT";
    /** 缺陷库导出（xlsx） */
    public static final String EXPORT_TYPE_DEFECT_STORAGE = "DEFECT_STORAGE";
    /**
     * 缺陷管理导出（xlsx）
     */
    public static final String EXPORT_TYPE_DEFECT_MANAGE = "DEFECT_MANAGE";

    /** 导出任务编号前缀 */
    public static final String EXPORT_TASK_PREFIX = "EXP-";

    public static final int FAILED = -1;
    public static final int COMPLETED = 100;

    public static void send(Long userId, String exportType, int progress, AtomicInteger lastProgress) {
        if (null == userId) {
            return;
        }
        // 中间进度按 5% 粒度去重，避免逐行推送把 websocket 打满
        int normalized = isTerminal(progress) ? progress : progress / 5 * 5;
        if (lastProgress != null && lastProgress.getAndSet(normalized) == normalized) {
            return;
        }
        Map<String, Object> progressMap = new HashMap<>(3);
        progressMap.put("exportType", exportType);
        progressMap.put("exportProgress", normalized);
        progressMap.put("exportStatus", isTerminal(progress)
                ? (normalized < 0 ? "FAILED" : "SUCCESS") : "PROCESSING");
        WebSocketSessionManager.broadcastMsg(WebSocketMsg.msg("EXPORT_PROGRESS", userId + "", progressMap));
    }

    private static boolean isTerminal(int progress) {
        return progress < 0 || progress >= COMPLETED;
    }
}
