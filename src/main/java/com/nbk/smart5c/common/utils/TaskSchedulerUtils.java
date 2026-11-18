package com.nbk.smart5c.common.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class TaskSchedulerUtils {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10); // 调整线程池大小以适应多任务
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

    /**
     * 调度一个延迟任务，在指定的时间后执行。
     *
     * @param taskId         任务的唯一标识符
     * @param delayInSeconds 延迟时间，单位为秒。
     * @param task           需要执行的任务。
     */
    public void scheduleTask(String taskId, long delayInSeconds, Runnable task) {
        // 如果该任务ID已存在，则先取消旧任务
        if (scheduledTasks.containsKey(taskId)) {
            cancelTask(taskId);
        }

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            try {
                task.run();
            } catch (Exception e) {
                System.err.println("任务[" + taskId + "]执行过程中发生错误: " + e.getMessage());

                // 更详细的错误处理逻辑可以根据需要添加
            } finally {
                // 在任务完成后从map中移除
                scheduledTasks.remove(taskId);
            }
        }, delayInSeconds, TimeUnit.SECONDS);

        scheduledTasks.put(taskId, future);
        System.out.println("任务[" + taskId + "]已安排在 " + delayInSeconds + " 秒后执行...");
    }

    /**
     * 取消指定ID的任务。
     *
     * @param taskId 任务的唯一标识符
     */
    public void cancelTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.get(taskId);
        if (future != null && !future.isCancelled()) {
            boolean mayInterruptIfRunning = true;
            future.cancel(mayInterruptIfRunning);
            scheduledTasks.remove(taskId);
            System.out.println("任务[" + taskId + "]已被取消");
        }
    }

    /**
     * 关闭调度器并释放资源。
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}