package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务复核详情视图对象
 * @since 2026-04-07 16:41:49
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "任务复核详情视图对象")
public class TaskReviewDetailVO {

    @ApiModelProperty(value = "任务")
    private Long taskId;

    @ApiModelProperty(value = "受电工状态")
    private Integer pantographStatus;

    @ApiModelProperty(value = "机车号")
    private String locomotiveNumber;

    @ApiModelProperty(value = "巡检图片详情")
    private List<ReviewPictureVO> reviewPictureList;

    @ApiModelProperty(value = "统计信息")
    private List<DefectCountVO> defectCountList;

    @ApiModelProperty(value = "巡检编号排序列表")
    private List<Long> inspectionIdList;

    @Data
    @Accessors(chain = true)
    @ApiModel(value = "统计信息视图对象")
    public static class DefectCountVO {
        @ApiModelProperty(value = "类别")
        private String category;
        @ApiModelProperty(value = "识别数量")
        private Integer identifiedNum;
        @ApiModelProperty(value = "确认数量")
        private Integer confirmNum;
    }
}
