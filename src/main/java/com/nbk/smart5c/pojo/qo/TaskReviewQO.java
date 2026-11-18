package com.nbk.smart5c.pojo.qo;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务复核查询对象
 * @since 2026-05-19 16:16:20
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "任务复核查询对象")
public class TaskReviewQO {
    @ApiModelProperty(value = "任务编号")
    private Long taskId;
    @ApiModelProperty(value = "缺陷类型列表")
    private List<String> defectTypeList;
    @ApiModelProperty(value = "确认状态(0:未查看,1:已查看,2:已确认)")
    private List<Integer> confirmStatusList;
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    @ApiModelProperty(value = "机车号")
    private String locomotiveNumber;
    @ApiModelProperty(value = "受电工状态")
    private Integer pantographStatus;

    @ApiModelProperty(name = "pageNo", value = "页号", required = true)
    private Integer pageNo;
    @ApiModelProperty(name = "pageSize", value = "页大小", required = true)
    private Integer pageSize;

    private Integer offset;//偏移量

    public Integer getPageNo() {
        if (ObjectUtil.isNull(pageNo)) {
            return 1;
        }
        return pageNo;
    }

    public Integer getPageSize() {
        if (ObjectUtil.isNull(pageSize)) {
            return 10;
        }
        return pageSize;
    }

}
