package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 终端类型枚举
 * ＠author jiaxuebin
 * @Date 2024-04-29
 */
@Getter
public enum ClientTypeEnum {
    WEB(0, "WEB"),
    APP(1, "APP"),
    WECHAT(2, "WECHAT"),
    PAD(3, "PAD"),
    ;

    private final Integer code;
    private final String name;

    ClientTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (ClientTypeEnum clientTypeEnum : ClientTypeEnum.values()) {
            if (clientTypeEnum.code.equals(code)) {
                return clientTypeEnum.name;
            }
        }
        return "";
    }

    public static List<Map<String, Object>> getClientTypeList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (ClientTypeEnum clientTypeEnum : ClientTypeEnum.values()) {
            Map<String, Object> limitMap = new HashMap<>();
            limitMap.put("code", clientTypeEnum.code);
            limitMap.put("name", clientTypeEnum.name);
            resultList.add(limitMap);
        }
        return resultList;
    }

    public static ClientTypeEnum getInstanceByCode(Integer code) {
        for (ClientTypeEnum clientTypeEnum : ClientTypeEnum.values()) {
            if (clientTypeEnum.code.equals(code)) {
                return clientTypeEnum;
            }
        }
        return null;
    }
}
