package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务复核下一个文件夹视图对象
 * @since 2026-04-07 16:41:49
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "任务复核下一个文件夹视图对象")
public class TaskReviewNextFolderVO {

    @ApiModelProperty(value = "机车号")
    private String locomotiveNumber;

    @ApiModelProperty(value = "受电工状态")
    private String pantographStatus;

    @ApiModelProperty(value = "缺陷描述")
    private String problemDesc;

    @ApiModelProperty(value = "巡检图片详情")
    private List<ReviewPictureVO> reviewPictureList;
}
