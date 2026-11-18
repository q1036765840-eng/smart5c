package com.nbk.smart5c.common.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class WebSocketSessionManager {

    /** key 为 sessionId，value 为连接 session（已用 ConcurrentWebSocketSessionDecorator 包装） */
    private static final Map<String, WebSocketSession> SESSION_POOL = new ConcurrentHashMap<>();

    /** 每个连接一个串行投递队列，保证同一连接的消息顺序，同时慢连接不阻塞其它连接 */
    private static final Map<String, SerialRunner> SEND_QUEUES = new ConcurrentHashMap<>();

    private static final int BROADCAST_THREADS = 4;

    /**
     * 线程池满时抛 RejectedExecutionException（由 SerialRunner 捕获并复位状态），
     * 不能用 DiscardPolicy——静默丢弃会让该连接的任务队列永久卡住。
     */
    private static final ExecutorService BROADCAST_EXECUTOR = new ThreadPoolExecutor(
            BROADCAST_THREADS, BROADCAST_THREADS,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(2000),
            new NamedDaemonThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    /**
     * 单条消息，同步发送。仅用于 handler 内部的应答（此时发送方就是该连接自身的事件线程）。
     */
    private static void sendMsg(WebSocketSession session, WebSocketMessage<?> text) {
        if (Objects.isNull(session) || !session.isOpen() || Objects.isNull(text)) {
            log.debug("跳过向不可用连接发送 websocket 数据: {}", text);
            return;
        }
        try {
            session.sendMessage(text);
        } catch (IOException | RuntimeException e) {
            log.warn("websocket 消息发送失败, sessionId={}: {}", session.getId(), e.getMessage());
            // 单个连接发送失败/已断开不应影响其它连接，直接清理
            removeAndClose(session.getId());
        }
    }

    /**
     * 广播消息。每个连接独立串行投递，单连接慢或失败不影响其它连接。
     */
    public static void broadcastMsg(String text) {
        if (Objects.isNull(text) || SESSION_POOL.isEmpty()) {
            return;
        }
        TextMessage message = new TextMessage(text);
        for (WebSocketSession session : SESSION_POOL.values()) {
            enqueue(session.getId(), session, message);
        }
    }

    /**
     * 给指定 sessionId 发消息，异步投递。sessionId 不存在时静默忽略。
     */
    public static void sendTo(String sessionId, String text) {
        WebSocketSession session = get(sessionId);
        if (session == null || Objects.isNull(text)) {
            return;
        }
        enqueue(sessionId, session, new TextMessage(text));
    }

    private static void enqueue(String sessionId, WebSocketSession session, TextMessage message) {
        // 已关闭的连接不再建队列，避免清理后的 sessionId 又被残留队列占住
        if (!session.isOpen()) {
            return;
        }
        SEND_QUEUES.computeIfAbsent(sessionId, k -> new SerialRunner())
                .execute(() -> sendMsg(session, message), sessionId);
    }

    public static void add(String key, WebSocketSession session) {
        if (key == null || session == null) {
            return;
        }
        SESSION_POOL.put(key, session);
    }

    private static WebSocketSession get(String key) {
        return key == null ? null : SESSION_POOL.get(key);
    }

    /**
     * 删除 session,会返回删除的 session
     */
    public static WebSocketSession remove(String key) {
        if (key == null) {
            return null;
        }
        SEND_QUEUES.remove(key);
        return SESSION_POOL.remove(key);
    }

    /**
     * 删除并关闭连接
     */
    public static void removeAndClose(String key) {
        WebSocketSession session = remove(key);
        if (session == null) {
            return;
        }
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException | RuntimeException e) {
            log.warn("关闭 websocket 连接失败, sessionId={}: {}", key, e.getMessage());
        }
    }

    public static int size() {
        return SESSION_POOL.size();
    }

    /**
     * 单连接串行执行器：提交的任务按 FIFO 顺序执行，同一时刻只有一个任务占用池线程。
     */
    private static final class SerialRunner {
        private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
        private final AtomicBoolean draining = new AtomicBoolean(false);

        void execute(Runnable task, String sessionId) {
            tasks.offer(task);
            if (draining.compareAndSet(false, true)) {
                scheduleDrain(sessionId);
            }
        }

        private void scheduleDrain(String sessionId) {
            try {
                BROADCAST_EXECUTOR.execute(() -> drain(sessionId));
            } catch (RejectedExecutionException e) {
                // 线程池已满：丢弃积压消息（进度类推送过期后无意义），并复位状态等待下次调度
                draining.set(false);
                int dropped = 0;
                while (tasks.poll() != null) {
                    dropped++;
                }
                log.warn("websocket 广播任务被拒绝, sessionId={}, 丢弃 {} 条积压消息", sessionId, dropped);
            }
        }

        private void drain(String sessionId) {
            try {
                Runnable task;
                while ((task = tasks.poll()) != null) {
                    try {
                        task.run();
                    } catch (RuntimeException e) {
                        log.warn("websocket 推送任务执行异常, sessionId={}: {}", sessionId, e.getMessage());
                    }
                }
            } finally {
                draining.set(false);
                // 收尾阶段有新任务进来时重新调度，避免任务滞留在队列
                if (!tasks.isEmpty() && draining.compareAndSet(false, true)) {
                    scheduleDrain(sessionId);
                }
            }
        }
    }

    private static final class NamedDaemonThreadFactory implements ThreadFactory {
        private final AtomicInteger index = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "ws-broadcast-" + index.incrementAndGet());
            t.setDaemon(true);
            return t;
        }
    }
}
