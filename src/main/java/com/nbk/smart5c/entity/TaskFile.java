package com.nbk.smart5c.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务文件表实体
 * @since 2026-05-19 19:52:09
 */
@Data
@Accessors(chain = true)
@TableName("nbk_task_file")
@ApiModel("任务文件表实体")
public class TaskFile {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务Id")
    @TableField("task_id")
    private Long taskId;

    @ApiModelProperty(value = "文件路径")
    @TableField("file_path")
    private String filePath;

}
