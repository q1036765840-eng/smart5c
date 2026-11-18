package com.nbk.smart5c.pojo.bo;

import lombok.Data;

/**
 * 5C 分析服务器通过 MQTT 推送的车次消息
 */
@Data
public class MqttTrainMessageBO {

    private Integer version;
    private String seg;
    private String trainPath;
    private String trainDir;
    private String trainId;
    private String trainDate;
    private String km;
    private String plateNumber;
    private Integer photoCount;
    private String sourceHost;
    private Long timestamp;
}
