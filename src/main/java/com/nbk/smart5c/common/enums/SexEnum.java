package com.nbk.smart5c.common.enums;

import lombok.Getter;

/**
 * @Desc 性别枚举
 * ＠author jiaxuebin
 * @Date 2024-04-25
 */
@Getter
public enum SexEnum {
    FEMALE(0, "女"),
    MALE(1, "男");

    private final Integer code;
    private final String name;

    SexEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (SexEnum sexNum : SexEnum.values()) {
            if (sexNum.code.equals(code)) {
                return sexNum.name;
            }
        }
        return "";
    }

    public static Integer getCodeByName(String name) {
        if (null == name) {
            return null;
        }
        for (SexEnum sexNum : SexEnum.values()) {
            if (sexNum.name.equals(name)) {
                return sexNum.code;
            }
        }
        return null;
    }

    public static SexEnum getInstanceByCode(Integer code) {
        for (SexEnum sexNum : SexEnum.values()) {
            if (sexNum.code.equals(code)) {
                return sexNum;
            }
        }
        return null;
    }
}
