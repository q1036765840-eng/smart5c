package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 职位枚举
 * ＠author jiaxuebin
 * @Date 2024-04-25
 */
@Getter
public enum PositionEnum {
    //职位(0:系统管理员,1:技术专工,2:检测员,3:只读用户)
    ADMIN(0, "系统管理员"),
    JSZG(1, "技术专工"),
    JCY(2, "检测员"),
    ZDYH(3, "只读用户"),
    ;

    private final Integer code;
    private final String name;

    PositionEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (PositionEnum positionEnum : PositionEnum.values()) {
            if (positionEnum.code.equals(code)) {
                return positionEnum.name;
            }
        }
        return "";
    }

    public static PositionEnum getInstanceByCode(Integer code) {
        for (PositionEnum positionEnum : PositionEnum.values()) {
            if (positionEnum.code.equals(code)) {
                return positionEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getPositionList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (PositionEnum positionEnum : PositionEnum.values()) {
            Map<String, Object> postionMap = new HashMap<>();
            postionMap.put("code", positionEnum.code);
            postionMap.put("name", positionEnum.name);
            resultList.add(postionMap);
        }
        return resultList;
    }
}
