package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 机构类型枚举
 * ＠author jiaxuebin
 * @Date 2024-04-29
 */
@Getter
public enum OrgTypeEnum {
    //0:线路,1:变电站,2:控制区,3:摄像头,4:预置点
    LINE(0, "线路"),
    SUBSTATION(1, "变电所"),
    REGION(2, "控制区");

    private final Integer code;
    private final String name;

    OrgTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (OrgTypeEnum orgTypeEnum : OrgTypeEnum.values()) {
            if (orgTypeEnum.code.equals(code)) {
                return orgTypeEnum.name;
            }
        }
        return "";
    }

    public static List<Map<String, Object>> getOrgTypeList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (OrgTypeEnum orgTypeEnum : OrgTypeEnum.values()) {
            Map<String, Object> orgMap = new HashMap<>();
            orgMap.put("code", orgTypeEnum.code);
            orgMap.put("name", orgTypeEnum.name);
            resultList.add(orgMap);
        }
        return resultList;
    }

    public static OrgTypeEnum getInstanceByCode(Integer code) {
        for (OrgTypeEnum orgTypeEnum : OrgTypeEnum.values()) {
            if (orgTypeEnum.code.equals(code)) {
                return orgTypeEnum;
            }
        }
        return null;
    }
}
