package com.nbk.smart5c.common.mqtt;

import cn.hutool.core.thread.NamedThreadFactory;
import com.nbk.smart5c.properties.MqttProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 车次目录同步消费者：固定延时（默认 2 秒）从阻塞队列取一个目录拷贝并解析。
 * 使用独立单线程调度器，避免阻塞/解析耗时占用 {@code @Scheduled} 的公共调度线程。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqttSyncScheduler implements SmartLifecycle {

    private final MqttProperties mqttProperties;
    private final MqttTrainMessageHandler messageHandler;
    private ScheduledThreadPoolExecutor executor;
    private volatile boolean running = false;

    @Override
    public void start() {
        if (!mqttProperties.isEnabled()) {
            running = true;
            return;
        }
        executor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("5c-mqtt-sync-", true));
        long delay = mqttProperties.getConsumeDelay();
        executor.scheduleWithFixedDelay(() -> {
            try {
                messageHandler.consumeOne();
            } catch (Exception e) {
                log.error("[mqtt] 同步队列消费异常", e);
            }
        }, delay, delay, TimeUnit.MILLISECONDS);
        running = true;
        log.info("[mqtt] 同步队列消费者启动，固定延时 {} ms", delay);
    }

    @Override
    public void stop() {
        if (null != executor) {
            executor.shutdownNow();
            executor = null;
        }
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
