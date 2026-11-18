package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 阅读状态枚举
 * ＠author jiaxuebin
 * @Date 2024-04-25
 */
@Getter
public enum ReadStatusEnum {
    NO(0, "未读"),
    YES(1, "已读");

    private final Integer code;
    private final String name;

    ReadStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (ReadStatusEnum readStatusEnum : ReadStatusEnum.values()) {
            if (readStatusEnum.code.equals(code)) {
                return readStatusEnum.name;
            }
        }
        return "";
    }

    public static ReadStatusEnum getInstanceByCode(Integer code) {
        for (ReadStatusEnum readStatusEnum : ReadStatusEnum.values()) {
            if (readStatusEnum.code.equals(code)) {
                return readStatusEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getDownList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (ReadStatusEnum readStatusEnum : ReadStatusEnum.values()) {
            Map<String, Object> readStatusMap = new HashMap<>();
            readStatusMap.put("code", readStatusEnum.code);
            readStatusMap.put("name", readStatusEnum.name);
            resultList.add(readStatusMap);
        }
        return resultList;
    }

}
