package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 逻辑是否枚举
 * ＠author jiaxuebin
 * @Date 2024-04-25
 */
@Getter
public enum LogicStatusEnum {
    NO(0, "否"),
    YES(1, "是");

    private final Integer code;
    private final String name;

    LogicStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (LogicStatusEnum logicStatusEnum : LogicStatusEnum.values()) {
            if (logicStatusEnum.code.equals(code)) {
                return logicStatusEnum.name;
            }
        }
        return "";
    }

    public static LogicStatusEnum getInstanceByCode(Integer code) {
        for (LogicStatusEnum logicStatusEnum : LogicStatusEnum.values()) {
            if (logicStatusEnum.code.equals(code)) {
                return logicStatusEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getDownList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (LogicStatusEnum logicStatusEnum : LogicStatusEnum.values()) {
            Map<String, Object> logicStatusMap = new HashMap<>();
            logicStatusMap.put("code", logicStatusEnum.code);
            logicStatusMap.put("name", logicStatusEnum.name);
            resultList.add(logicStatusMap);
        }
        return resultList;
    }

}
