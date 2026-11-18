package com.nbk.smart5c.common.enums;

import lombok.Getter;

/**
 * @Desc 线路行别枚举
 * ＠author jiaxuebin
 * @Date 2024-04-25
 */
@Getter
public enum LineDirectionEnum {
    UP(0, "上行"),
    DOWN(1, "下行"),
    SINGLE(2, "单线"),
    OTHER(3, "其它"),
    ;

    private final Integer code;
    private final String name;

    LineDirectionEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (LineDirectionEnum directionEnum : LineDirectionEnum.values()) {
            if (directionEnum.code.equals(code)) {
                return directionEnum.name;
            }
        }
        return "";
    }

    public static Integer getCodeByName(String name) {
        if (null == name) {
            return null;
        }
        for (LineDirectionEnum directionEnum : LineDirectionEnum.values()) {
            if (directionEnum.name.equals(name)) {
                return directionEnum.code;
            }
        }
        return null;
    }

    public static LineDirectionEnum getInstanceByCode(Integer code) {
        for (LineDirectionEnum directionEnum : LineDirectionEnum.values()) {
            if (directionEnum.code.equals(code)) {
                return directionEnum;
            }
        }
        return null;
    }
}
