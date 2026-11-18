package com.nbk.smart5c.common.enums;

import lombok.Getter;

/**
 * @Desc 锁定状态
 * ＠author jiaxuebin
 * @Date 2024-04-25
 */
@Getter
public enum LockStatusEnum {
    UNLOCKED(0, "未锁定"),
    LOCKED(1, "已锁定");
    private final Integer code;
    private final String name;

    LockStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (LockStatusEnum ipcTypeEnum : LockStatusEnum.values()) {
            if (ipcTypeEnum.code.equals(code)) {
                return ipcTypeEnum.name;
            }
        }
        return "";
    }

    public static LockStatusEnum getInstanceByCode(Integer code) {
        for (LockStatusEnum ipcTypeEnum : LockStatusEnum.values()) {
            if (ipcTypeEnum.code.equals(code)) {
                return ipcTypeEnum;
            }
        }
        return null;
    }
}
