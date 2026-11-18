package com.nbk.smart5c.pojo.qo;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务表查询对象
 * @since 2026-04-07 16:41:49
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "任务表查询对象")
public class TaskQO {
    @ApiModelProperty(value = "任务名")
    private String taskName;
    @ApiModelProperty(value = "0:低,1:中,2:高")
    private Integer priority;
    @ApiModelProperty(value = "任务状态(0:创建中,1:待分析,2:分析中,3:暂停,4:分析完成)")
    private Integer taskStatus;
    @ApiModelProperty(name = "startTime", value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @ApiModelProperty(name = "endTime", value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    @ApiModelProperty(name = "reviewStatus", value = "复核进度(0:未完成,1:已完成)")
    private Integer reviewStatus;
    @ApiModelProperty(name = "pageNo", value = "页号", required = true)
    private Integer pageNo;
    @ApiModelProperty(name = "pageSize", value = "页大小", required = true)
    private Integer pageSize;

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
