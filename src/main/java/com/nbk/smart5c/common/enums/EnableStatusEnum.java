package com.nbk.smart5c.common.enums;

import lombok.Getter;

/**
 * @Desc 启用/停用状态枚举
 * ＠author jiaxuebin
 * @Date 2024-04-25
 */
@Getter
public enum EnableStatusEnum {
    DISABLE(0, "停用"),
    ENABLE(1, "启用");

    private final Integer code;
    private final String name;

    EnableStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (EnableStatusEnum enableStatusEnum : EnableStatusEnum.values()) {
            if (enableStatusEnum.code.equals(code)) {
                return enableStatusEnum.name;
            }
        }
        return "";
    }

    public static EnableStatusEnum getInstanceByCode(Integer code) {
        for (EnableStatusEnum enableStatusEnum : EnableStatusEnum.values()) {
            if (enableStatusEnum.code.equals(code)) {
                return enableStatusEnum;
            }
        }
        return null;
    }
}
