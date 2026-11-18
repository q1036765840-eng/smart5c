package com.nbk.smart5c.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 5C 智能分析数据同步的 MQTT 订阅配置：每个段一个消费账号
 */
@Data
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {

    /**
     * 总开关
     */
    private boolean enabled = false;

    /**
     * 从队列取目录到解析的固定延时时长（毫秒）
     */
    private long consumeDelay = 2000L;

    /**
     * 目录同步队列容量
     */
    private int queueCapacity = 1000;

    /**
     * 订阅 QoS
     */
    private int qos = 1;

    /**
     * 连接超时（秒）
     */
    private int connectionTimeout = 10;

    /**
     * 心跳间隔（秒）
     */
    private int keepAliveInterval = 60;

    /**
     * 是否使用持久会话（cleanSession=false），断线期间的消息由 broker 保留
     */
    private boolean persistentSession = false;

    /**
     * 首次连接失败后的重试间隔（秒）
     */
    private int reconnectInterval = 10;

    /**
     * 各段订阅账号
     */
    private List<Segment> segments = new ArrayList<>();

    @Data
    public static class Segment {
        /**
         * 消息体中的 seg 值，如 qd/jn/wg
         */
        private String name;
        /**
         * NFS 中该段对应的目录名，如 qingdao/jinan/weiguan
         */
        private String segmentDir;
        private String url;
        private String topic;
        private String username;
        private String password;
        private String clientId;
    }
}
