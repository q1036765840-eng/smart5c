package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 任务等级枚举
 * ＠author jiaxuebin
 * @Date 2026-05-19
 */
@Getter
public enum TaskLevelEnum {
    LOW(0, "低"),
    MIDDLE(1, "中"),
    HIGH(2, "高");

    private final Integer code;
    private final String name;

    TaskLevelEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (TaskLevelEnum taskLevelEnum : TaskLevelEnum.values()) {
            if (taskLevelEnum.code.equals(code)) {
                return taskLevelEnum.name;
            }
        }
        return "";
    }

    public static TaskLevelEnum getInstanceByCode(Integer code) {
        for (TaskLevelEnum taskLevelEnum : TaskLevelEnum.values()) {
            if (taskLevelEnum.code.equals(code)) {
                return taskLevelEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getLevelList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (TaskLevelEnum taskLevelEnum : TaskLevelEnum.values()) {
            Map<String, Object> taskLevelMap = new HashMap<>();
            taskLevelMap.put("code", taskLevelEnum.code);
            taskLevelMap.put("name", taskLevelEnum.name);
            resultList.add(taskLevelMap);
        }
        return resultList;
    }

}
