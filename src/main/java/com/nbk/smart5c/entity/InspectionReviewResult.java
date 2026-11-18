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
 * @apiNote 巡查数据审核结果表实体
 * @since 2026-01-29 10:35:00
 */
@Data
@Accessors(chain = true)
@TableName("nbk_inspection_review_result")
@ApiModel("巡查数据审核结果表实体")
public class InspectionReviewResult extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "巡检id")
    @TableField("inspection_id")
    private Long inspectionId;

    @ApiModelProperty(value = "缺陷图路径")
    @TableField("image_path")
    private String imagePath;

    @ApiModelProperty(value = "缺陷描述")
    @TableField("problem_desc")
    private String problemDesc;

    @ApiModelProperty(value = "受电弓状态(2:异常,1:正常)")
    @TableField("pantograph_status")
    private Integer pantographStatus;

    @ApiModelProperty(value = "缺陷等级")
    @TableField("defect_level")
    private Integer defectLevel;

    @ApiModelProperty(value = "图片是否合规")
    @TableField("image_valid")
    private Integer imageValid;

    @ApiModelProperty(value = "标记区域")
    @TableField("marked_regions")
    private String markedRegions;

    @ApiModelProperty(value = "机车号")
    @TableField("locomotive_number")
    private String locomotiveNumber;

    @ApiModelProperty(value = "置信度")
    @TableField("reliability")
    private String reliability;

    @ApiModelProperty(value = "确认状态(0:未查看,1:已查看,2:已确认,3:误报)")
    @TableField("confirm_status")
    private Integer confirmStatus;

    @ApiModelProperty(value = "任务id")
    @TableField("task_id")
    private Long taskId;

    @ApiModelProperty(value = "文件网络路径")
    @TableField("net_path")
    private String netPath;

    @ApiModelProperty(value = "是否误报")
    @TableField("misreport")
    private Integer misreport;
}
