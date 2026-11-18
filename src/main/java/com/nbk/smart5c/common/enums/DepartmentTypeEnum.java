package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 部门类型枚举
 * ＠author jiaxuebin
 * @Date 2024-04-25
 */
@Getter
public enum DepartmentTypeEnum {
    //部门类型(0:供电部,1:供电段,2:车间)
    GDB(0, "供电部"),
    GDD(1, "供电段"),
    CJ(2, "车间");

    private final Integer code;
    private final String name;

    DepartmentTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (DepartmentTypeEnum departmentTypeEnum : DepartmentTypeEnum.values()) {
            if (departmentTypeEnum.code.equals(code)) {
                return departmentTypeEnum.name;
            }
        }
        return "";
    }

    public static DepartmentTypeEnum getInstanceByCode(Integer code) {
        for (DepartmentTypeEnum departmentTypeEnum : DepartmentTypeEnum.values()) {
            if (departmentTypeEnum.code.equals(code)) {
                return departmentTypeEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getDepartTypeList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (DepartmentTypeEnum departmentTypeEnum : DepartmentTypeEnum.values()) {
            Map<String, Object> departMap = new HashMap<>();
            departMap.put("code", departmentTypeEnum.code);
            departMap.put("name", departmentTypeEnum.name);
            resultList.add(departMap);
        }
        return resultList;
    }
}
