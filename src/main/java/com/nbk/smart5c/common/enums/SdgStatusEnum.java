package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 受电弓状态枚举（列表/筛选用，与 PantographStatusEnum 独立）
 * ＠author jiaxuebin
 * @Date 2024-04-25
 */
@Getter
public enum SdgStatusEnum {
    ALL(0,"全部"),
    NORMAL(1,"正常"),
    FOREIGN(2,"异物"),
    BENDING(3,"弯曲"),
    BENGK(4,"崩块"),
    MISSING(5,"缺失"),
    ;

    private final Integer code;
    private final String name;

    SdgStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (SdgStatusEnum defectTypeEnum : SdgStatusEnum.values()) {
            if (defectTypeEnum.code.equals(code)) {
                return defectTypeEnum.name;
            }
        }
        return "";
    }
    public static SdgStatusEnum getInstanceByName(String name) {
        if (null == name) {
            return null;
        }
        for (SdgStatusEnum defectTypeEnum : SdgStatusEnum.values()) {
            if (defectTypeEnum.name.equals(name)) {
                return defectTypeEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getSdgStatusList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (SdgStatusEnum defectTypeEnum : SdgStatusEnum.values()) {
            Map<String, Object> defectTypeMap = new HashMap<>();
            defectTypeMap.put("code", defectTypeEnum.code);
            defectTypeMap.put("name", defectTypeEnum.name);
            resultList.add(defectTypeMap);
        }
        return resultList;
    }

}
