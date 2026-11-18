package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName: TaskFileVO
 * @Author: Jiaxb
 * @CreateTime: 2022-03-08
 * @Description: 任务文件信息视图对象
 */
@Data
@Accessors(chain = true)
@ApiModel("任务文件信息视图对象")
public class TaskFileVO {

    /**
     * 文件路径
     */
    @ApiModelProperty(name = "filePath", value = "文件路径")
    private String filePath;

    /**
     * 文件名
     */
    @ApiModelProperty(name = "fileName", value = "文件名")
    private String fileName;

    /**
     * 文件数量
     */
    @ApiModelProperty(name = "fileNum", value = "文件数量")
    private Long fileNum;
}
