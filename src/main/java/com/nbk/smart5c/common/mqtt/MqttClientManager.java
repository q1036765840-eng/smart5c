package com.nbk.smart5c.common.mqtt;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.nbk.smart5c.properties.MqttProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 各段 5C 分析服务器 MQTT 订阅客户端管理：启动时按配置为每个段建立连接并订阅主题。
 * <p>
 * 首次连接在后台重试（paho 的自动重连只在首次连接成功后生效）；连接成功后的断线由 paho 自动重连，
 * 并在 {@code connectComplete} 回调中重新订阅。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(MqttProperties.class)
public class MqttClientManager implements SmartLifecycle {

    private final MqttProperties mqttProperties;
    private final MqttTrainMessageHandler messageHandler;
    private final List<MqttClient> clients = new ArrayList<>();
    private ScheduledThreadPoolExecutor connectExecutor;
    private volatile boolean running = false;

    @Override
    public void start() {
        if (!mqttProperties.isEnabled()) {
            log.info("[mqtt] 未开启，跳过各段订阅");
            running = true;
            return;
        }
        if (mqttProperties.getSegments().isEmpty()) {
            log.warn("[mqtt] 已开启但未配置订阅段");
            running = true;
            return;
        }
        running = true;
        connectExecutor = new ScheduledThreadPoolExecutor(mqttProperties.getSegments().size(),
                new NamedThreadFactory("5c-mqtt-conn-", true));
        for (MqttProperties.Segment segment : mqttProperties.getSegments()) {
            if (StrUtil.hasBlank(segment.getName(), segment.getSegmentDir(), segment.getUrl(),
                    segment.getTopic(), segment.getClientId())) {
                log.warn("[mqtt] 段配置不完整，跳过: {}", segment);
                continue;
            }
            MqttClient client;
            try {
                client = createClient(segment);
            } catch (MqttException e) {
                log.error("[mqtt][{}] 创建客户端失败: {}", segment.getName(), e.getMessage(), e);
                continue;
            }
            clients.add(client);
            connectWithRetry(segment, client);
        }
        log.info("[mqtt] 订阅客户端启动完成，共 {} 个连接", clients.size());
    }

    private MqttClient createClient(MqttProperties.Segment segment) throws MqttException {
        MqttClient client = new MqttClient(segment.getUrl(), segment.getClientId(), new MemoryPersistence());
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                // 首次连接与自动重连成功都会回调，在此统一订阅，保证重连后订阅关系恢复
                try {
                    client.subscribe(segment.getTopic(), mqttProperties.getQos(), (topic, message) -> {
                        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                        log.info("[mqtt][{}] 收到消息, topic={}", segment.getName(), topic);
                        log.debug("[mqtt][{}] 消息内容: {}", segment.getName(), payload);
                        messageHandler.handleMessage(segment, payload);
                    });
                    log.info("[mqtt][{}] {} {}，已订阅 {}", segment.getName(), serverURI,
                            reconnect ? "重连成功" : "连接成功", segment.getTopic());
                } catch (MqttException e) {
                    log.error("[mqtt][{}] 订阅失败: {}", segment.getName(), segment.getTopic(), e);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                log.warn("[mqtt][{}] 连接断开，等待自动重连: {}", segment.getName(),
                        null == cause ? "unknown" : cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                // 消息处理在订阅回调中完成，此处仅满足接口约定
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // 仅订阅，不发布
            }
        });
        return client;
    }

    /**
     * 首次连接失败时按重试间隔持续尝试，直到连接成功；连接成功后由 paho 负责断线重连
     */
    private void connectWithRetry(MqttProperties.Segment segment, MqttClient client) {
        connectExecutor.execute(() -> {
            while (!client.isConnected() && running) {
                try {
                    client.connect(buildOptions(segment));
                    log.info("[mqtt][{}] 初始连接成功: {}", segment.getName(), segment.getUrl());
                    return;
                } catch (MqttException e) {
                    log.warn("[mqtt][{}] 连接失败，{} 秒后重试: {}", segment.getName(),
                            mqttProperties.getReconnectInterval(), e.getMessage());
                    try {
                        TimeUnit.SECONDS.sleep(mqttProperties.getReconnectInterval());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        });
    }

    private MqttConnectOptions buildOptions(MqttProperties.Segment segment) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(segment.getUsername());
        options.setPassword(segment.getPassword() == null ? null : segment.getPassword().toCharArray());
        options.setCleanSession(!mqttProperties.isPersistentSession());
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(mqttProperties.getConnectionTimeout());
        options.setKeepAliveInterval(mqttProperties.getKeepAliveInterval());
        return options;
    }

    @Override
    public void stop() {
        running = false;
        if (null != connectExecutor) {
            connectExecutor.shutdownNow();
            connectExecutor = null;
        }
        for (MqttClient client : clients) {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                }
                // 可能仍有首次连接在途，强制关闭避免阻塞停机
                client.close(true);
            } catch (MqttException e) {
                log.debug("[mqtt] 关闭客户端异常: {}", e.getMessage());
            }
        }
        clients.clear();
        log.info("[mqtt] 订阅客户端已关闭");
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
