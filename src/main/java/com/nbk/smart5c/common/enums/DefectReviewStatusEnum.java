package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 缺陷审核枚举
 * ＠author jiaxuebin
 * @Date 2024-04-25
 */
@Getter
public enum DefectReviewStatusEnum {
    NO_REVIEW(0, "未审核"),
    PASS(1, "通过"),
    REJECT(2, "不通过"),
    ;

    private final Integer code;
    private final String name;

    DefectReviewStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (DefectReviewStatusEnum reviewStatusEnum : DefectReviewStatusEnum.values()) {
            if (reviewStatusEnum.code.equals(code)) {
                return reviewStatusEnum.name;
            }
        }
        return "";
    }

    public static DefectReviewStatusEnum getInstanceByCode(Integer code) {
        for (DefectReviewStatusEnum reviewStatusEnum : DefectReviewStatusEnum.values()) {
            if (reviewStatusEnum.code.equals(code)) {
                return reviewStatusEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getReviewStatusList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (DefectReviewStatusEnum reviewStatusEnum : DefectReviewStatusEnum.values()) {
            Map<String, Object> reviewStatusMap = new HashMap<>();
            reviewStatusMap.put("code", reviewStatusEnum.code);
            reviewStatusMap.put("name", reviewStatusEnum.name);
            resultList.add(reviewStatusMap);
        }
        return resultList;
    }
}
