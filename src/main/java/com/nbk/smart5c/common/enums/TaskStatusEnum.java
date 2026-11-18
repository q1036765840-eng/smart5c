package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 任务状态枚举
 * ＠author jiaxuebin
 * @Date 2024-10-21
 */
@Getter
public enum TaskStatusEnum {
    //任务状态(0:创建中,1:待分析,2:分析中,3:暂停,4:分析完成,9:等待中)
    CREATING(0, "创建中"),
    WAIT_CHECK(1, "待分析"),
    CHECKING(2, "分析中"),
    PAUSE(3, "暂停"),
    FINISH(4, "分析完成"),
    WAIT_QUEUE(9, "等待中");

    private final Integer code;
    private final String name;

    TaskStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (TaskStatusEnum taskStatusEnum : TaskStatusEnum.values()) {
            if (taskStatusEnum.code.equals(code)) {
                return taskStatusEnum.name;
            }
        }
        return "";
    }

    public static List<Map<String, Object>> getTaskStatusList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (TaskStatusEnum taskStatusEnum : TaskStatusEnum.values()) {
            Map<String, Object> taskStatusMap = new HashMap<>();
            taskStatusMap.put("code", taskStatusEnum.code);
            taskStatusMap.put("name", taskStatusEnum.name);
            resultList.add(taskStatusMap);
        }
        return resultList;
    }

    public static TaskStatusEnum getInstanceByCode(Integer code) {
        for (TaskStatusEnum taskStatusEnum : TaskStatusEnum.values()) {
            if (taskStatusEnum.code.equals(code)) {
                return taskStatusEnum;
            }
        }
        return null;
    }
}
