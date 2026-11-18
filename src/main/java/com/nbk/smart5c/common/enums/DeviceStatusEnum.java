package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 设备状态枚举
 * ＠author jiaxuebin
 * @Date 2024-04-25
 */
@Getter
public enum DeviceStatusEnum {
    STOP(0, "停用"),
    RUN(1, "运行"),
    REPAIR(2, "维修"),
    ;

    private final Integer code;
    private final String name;

    DeviceStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (DeviceStatusEnum deviceStatusEnum : DeviceStatusEnum.values()) {
            if (deviceStatusEnum.code.equals(code)) {
                return deviceStatusEnum.name;
            }
        }
        return "";
    }

    public static DeviceStatusEnum getInstanceByCode(Integer code) {
        for (DeviceStatusEnum deviceStatusEnum : DeviceStatusEnum.values()) {
            if (deviceStatusEnum.code.equals(code)) {
                return deviceStatusEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getDeviceStatusList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (DeviceStatusEnum deviceStatusEnum : DeviceStatusEnum.values()) {
            Map<String, Object> limitMap = new HashMap<>();
            limitMap.put("code", deviceStatusEnum.code);
            limitMap.put("name", deviceStatusEnum.name);
            resultList.add(limitMap);
        }
        return resultList;
    }

}
