package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 分析状态枚举
 * ＠author jiaxuebin
 * @Date 2024-07-05
 */
@Getter
public enum AnalyzeStatusEnum {
    //状态(0:待分析,1:待审批,2:已审批)
    WAIT_ANALYZE(0, "待分析"),
    WAIT_REVIEW(1, "待审批"),
    REVIEWED(2, "已审批");

    private final Integer code;
    private final String name;

    AnalyzeStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (AnalyzeStatusEnum analyzeStatusEnum : AnalyzeStatusEnum.values()) {
            if (analyzeStatusEnum.code.equals(code)) {
                return analyzeStatusEnum.name;
            }
        }
        return "";
    }

    public static List<Map<String, Object>> getAnalyzeStatusList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (AnalyzeStatusEnum analyzeStatusEnum : AnalyzeStatusEnum.values()) {
            Map<String, Object> analyzeStatusMap = new HashMap<>();
            analyzeStatusMap.put("code", analyzeStatusEnum.code);
            analyzeStatusMap.put("name", analyzeStatusEnum.name);
            resultList.add(analyzeStatusMap);
        }
        return resultList;
    }

    public static AnalyzeStatusEnum getInstanceByCode(Integer code) {
        for (AnalyzeStatusEnum analyzeStatusEnum : AnalyzeStatusEnum.values()) {
            if (analyzeStatusEnum.code.equals(code)) {
                return analyzeStatusEnum;
            }
        }
        return null;
    }
}
