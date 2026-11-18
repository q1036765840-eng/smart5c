package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务表修改业务对象
 * @since 2026-04-07 16:41:49
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "任务表修改业务对象")
public class TaskUpdateBO {

    @ApiModelProperty(value = "主键")
    @NotNull(message = "任务id不能为空")
    private Long id;

    @ApiModelProperty(value = "任务文件列表")
    private List<String> taskFiles;

    @ApiModelProperty(value = "优先级")
    private Integer priority;

    @ApiModelProperty(value = "截止时间")
    private LocalDateTime deadline;
}
