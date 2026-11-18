package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务表新增业务对象
 * @since 2026-04-07 16:41:49
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "任务表新增业务对象")
public class TaskSaveBO {

    @ApiModelProperty(value = "数据路径")
    private String dataPath;

    @ApiModelProperty(value = "不参与分析文件列表")
    private List<String> taskFiles;

    @ApiModelProperty(value = "优先级")
    private Integer priority;

    @ApiModelProperty(value = "截止时间")
    private LocalDateTime deadline;
}
