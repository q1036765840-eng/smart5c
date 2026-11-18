package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 受电弓状态枚举
 * ＠author jiaxuebin
 * @Date 2024-07-05
 */
@Getter
public enum PantographStatusEnum {
    //受电弓状态(正常:1,异常:2)
    UNKNOWN(0, "未分析"),
    NORMAL(1, "正常"),
    ERROR(2, "异常");

    private final Integer code;
    private final String name;

    PantographStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (PantographStatusEnum pantographStatusEnum : PantographStatusEnum.values()) {
            if (pantographStatusEnum.code.equals(code)) {
                return pantographStatusEnum.name;
            }
        }
        return "";
    }

    public static Integer getCodeByName(String name) {
        if (null == name) {
            return null;
        }
        for (PantographStatusEnum pantographStatusEnum : PantographStatusEnum.values()) {
            if (pantographStatusEnum.name.equals(name)) {
                return pantographStatusEnum.code;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getPantographStatusList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (PantographStatusEnum pantographStatusEnum : PantographStatusEnum.values()) {
            Map<String, Object> pantographStatusMap = new HashMap<>();
            pantographStatusMap.put("code", pantographStatusEnum.code);
            pantographStatusMap.put("name", pantographStatusEnum.name);
            resultList.add(pantographStatusMap);
        }
        return resultList;
    }

    public static PantographStatusEnum getInstanceByCode(Integer code) {
        for (PantographStatusEnum pantographStatusEnum : PantographStatusEnum.values()) {
            if (pantographStatusEnum.code.equals(code)) {
                return pantographStatusEnum;
            }
        }
        return null;
    }
}
