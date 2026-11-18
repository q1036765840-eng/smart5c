package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 推送任务创建业务对象
 * @since 2026-04-07 16:41:49
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "推送任务创建业务对象")
public class TaskPushSaveBO {

    @ApiModelProperty(value = "文件夹路径")
    private String folderPath;

    @ApiModelProperty(value = "优先级")
    private Integer priority;

    @ApiModelProperty(value = "截止时间")
    private LocalDateTime deadline;
}
