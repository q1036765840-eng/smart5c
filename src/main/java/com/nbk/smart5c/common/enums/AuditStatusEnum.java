package com.nbk.smart5c.common.enums;

import lombok.Getter;

/**
 * @Desc 审批状态枚举
 * ＠author jiaxuebin
 * @Date 2024-10-17
 */
@Getter
public enum AuditStatusEnum {
    //审批状态(0:待审核,1:通过,2:撤消)
    AUDITING(0, "待审核"),
    PASS(1, "通过"),
    REVOKED(2, "撤消"),
    REJECT(3, "驳回");

    private final Integer code;
    private final String name;

    AuditStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (AuditStatusEnum auditStatusEnum : AuditStatusEnum.values()) {
            if (auditStatusEnum.code.equals(code)) {
                return auditStatusEnum.name;
            }
        }
        return "";
    }

    public static AuditStatusEnum getInstanceByCode(Integer code) {
        for (AuditStatusEnum auditStatusEnum : AuditStatusEnum.values()) {
            if (auditStatusEnum.code.equals(code)) {
                return auditStatusEnum;
            }
        }
        return null;
    }
}
