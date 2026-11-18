package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 缺陷类型枚举
 * ＠author jiaxuebin
 * @Date 2024-04-25
 */
@Getter
public enum DefectTypeEnum {
    FOREIGN("C_YW", "异物"),
    BENDING("C_WQ", "弯曲"),
    BENGK("C_BK", "崩块"),
    MISSING("C_QS", "缺失"),
    ;

    private final String code;
    private final String name;

    DefectTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(String code) {
        if (null == code) {
            return "";
        }
        for (DefectTypeEnum defectTypeEnum : DefectTypeEnum.values()) {
            if (defectTypeEnum.code.equals(code)) {
                return defectTypeEnum.name;
            }
        }
        return "";
    }

    public static DefectTypeEnum getInstanceByCode(String code) {
        for (DefectTypeEnum defectTypeEnum : DefectTypeEnum.values()) {
            if (defectTypeEnum.code.equals(code)) {
                return defectTypeEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getDefectTypeList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (DefectTypeEnum defectTypeEnum : DefectTypeEnum.values()) {
            Map<String, Object> defectTypeMap = new HashMap<>();
            defectTypeMap.put("code", defectTypeEnum.code);
            defectTypeMap.put("name", defectTypeEnum.name);
            resultList.add(defectTypeMap);
        }
        return resultList;
    }

}
