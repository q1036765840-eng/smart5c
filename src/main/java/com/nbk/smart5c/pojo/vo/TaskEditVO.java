package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务编辑视图对象
 * @since 2026-04-07 16:41:49
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "任务编辑视图对象")
public class TaskEditVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "任务文件列表")
    private List<String> taskFiles;

    @ApiModelProperty(value = "优先级")
    private Integer priority;

    @ApiModelProperty(value = "截止时间")
    private LocalDateTime deadline;
}
