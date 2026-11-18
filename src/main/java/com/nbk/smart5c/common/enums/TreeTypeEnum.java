package com.nbk.smart5c.common.enums;

import lombok.Getter;

/**
 * @Desc 审批状态枚举
 * ＠author jiaxuebin
 * @Date 2024-10-17
 */
@Getter
public enum TreeTypeEnum {
    //树节点类型(1:供电段,2:线路,3:区站,4:支柱)
    SECTION(1, "供电段"),
    LINE(2, "线路"),
    STATION(3, "区站"),
    PILLAR(4, "支柱");

    private final Integer code;
    private final String name;

    TreeTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (TreeTypeEnum auditStatusEnum : TreeTypeEnum.values()) {
            if (auditStatusEnum.code.equals(code)) {
                return auditStatusEnum.name;
            }
        }
        return "";
    }

    public static TreeTypeEnum getInstanceByCode(Integer code) {
        for (TreeTypeEnum auditStatusEnum : TreeTypeEnum.values()) {
            if (auditStatusEnum.code.equals(code)) {
                return auditStatusEnum;
            }
        }
        return null;
    }
}
