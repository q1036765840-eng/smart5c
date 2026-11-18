package com.nbk.smart5c.pojo.qo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 缺陷库详情查询对象
 * @since 2026-01-29 10:35:00
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "缺陷库详情查询对象")
public class DefectStorageDetailQO {
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    @ApiModelProperty(value = "缺陷类型")
    private String defectType;
    @ApiModelProperty(value = "审核状态列表(0:未审核,1:通过,2:不通过)")
    private List<Integer> reviewStatusList;
    @ApiModelProperty(value = "缺陷入库id")
    private Long id;
}
