package com.nbk.smart5c.common.enums;

import lombok.Getter;

/**
 * @Desc 缺陷来源枚举
 * ＠author jiaxuebin
 * @Date 2026-10-17
 */
@Getter
public enum DefectSourceTypeEnum {
    //缺陷来源(0:其它未知,1:算法,2:人工)
    OTHER(0, "其它未知"),
    AI(1, "算法"),
    MANUAL(2, "人工"),
    ;

    private final Integer code;
    private final String name;

    DefectSourceTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (DefectSourceTypeEnum sourceTypeEnum : DefectSourceTypeEnum.values()) {
            if (sourceTypeEnum.code.equals(code)) {
                return sourceTypeEnum.name;
            }
        }
        return "";
    }

    public static DefectSourceTypeEnum getInstanceByCode(Integer code) {
        for (DefectSourceTypeEnum sourceTypeEnum : DefectSourceTypeEnum.values()) {
            if (sourceTypeEnum.code.equals(code)) {
                return sourceTypeEnum;
            }
        }
        return null;
    }
}
