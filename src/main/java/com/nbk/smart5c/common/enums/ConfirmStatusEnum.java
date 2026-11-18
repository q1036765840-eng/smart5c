package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 确认状态枚举
 * ＠author jiaxuebin
 * @Date 2024-04-25
 */
@Getter
public enum ConfirmStatusEnum {
    NO_VIEW(0, "未查看"),
    VIEWED(1, "已查看"),
    CONFIRMED(2, "已确认"),
    MISREPORT(3, "误报"),
    ;

    private final Integer code;
    private final String name;

    ConfirmStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (ConfirmStatusEnum confirmStatusEnum : ConfirmStatusEnum.values()) {
            if (confirmStatusEnum.code.equals(code)) {
                return confirmStatusEnum.name;
            }
        }
        return "";
    }

    public static ConfirmStatusEnum getInstanceByCode(Integer code) {
        for (ConfirmStatusEnum confirmStatusEnum : ConfirmStatusEnum.values()) {
            if (confirmStatusEnum.code.equals(code)) {
                return confirmStatusEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getConfirmStatusList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (ConfirmStatusEnum confirmStatusEnum : ConfirmStatusEnum.values()) {
            Map<String, Object> limitMap = new HashMap<>();
            limitMap.put("code", confirmStatusEnum.code);
            limitMap.put("name", confirmStatusEnum.name);
            resultList.add(limitMap);
        }
        return resultList;
    }

}
