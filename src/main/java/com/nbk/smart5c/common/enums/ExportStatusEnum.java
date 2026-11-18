package com.nbk.smart5c.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc 导出状态枚举
 * ＠author jiaxuebin
 * @Date 2026-10-27
 */
@Getter
public enum ExportStatusEnum {
    //导出状态(0:导出中,1:成功,2:失败)
    PROCESSING(0, "导出中"),
    SUCCESS(1, "成功"),
    FAILED(2, "失败"),
    ;

    private final Integer code;
    private final String name;

    ExportStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (null == code) {
            return "";
        }
        for (ExportStatusEnum exportStatusEnum : ExportStatusEnum.values()) {
            if (exportStatusEnum.code.equals(code)) {
                return exportStatusEnum.name;
            }
        }
        return "";
    }

    public static ExportStatusEnum getInstanceByCode(Integer code) {
        for (ExportStatusEnum exportStatusEnum : ExportStatusEnum.values()) {
            if (exportStatusEnum.code.equals(code)) {
                return exportStatusEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getExportStatusList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (ExportStatusEnum exportStatusEnum : ExportStatusEnum.values()) {
            Map<String, Object> exportStatusMap = new HashMap<>();
            exportStatusMap.put("code", exportStatusEnum.code);
            exportStatusMap.put("name", exportStatusEnum.name);
            resultList.add(exportStatusMap);
        }
        return resultList;
    }
}
