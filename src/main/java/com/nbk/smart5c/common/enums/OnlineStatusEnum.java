package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 摄像头是否在线
 * ＠author jiaxuebin
 * @Date 2024-04-25
 */
@Getter
public enum OnlineStatusEnum {
    OFFLINE(0, "离线"),
    ONLINE(1, "在线");
//    ASTABLE(2,"不稳定");

    private final Integer code;
    private final String name;

    OnlineStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (OnlineStatusEnum ipcTypeEnum : OnlineStatusEnum.values()) {
            if (ipcTypeEnum.code.equals(code)) {
                return ipcTypeEnum.name;
            }
        }
        return "";
    }

    public static OnlineStatusEnum getInstanceByCode(Integer code) {
        for (OnlineStatusEnum ipcTypeEnum : OnlineStatusEnum.values()) {
            if (ipcTypeEnum.code.equals(code)) {
                return ipcTypeEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getStatusList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (OnlineStatusEnum onlineStatusEnum : OnlineStatusEnum.values()) {
            Map<String, Object> statusMap = new HashMap<>();
            statusMap.put("code", onlineStatusEnum.code);
            statusMap.put("name", onlineStatusEnum.name);
            resultList.add(statusMap);
        }
        return resultList;
    }
}
