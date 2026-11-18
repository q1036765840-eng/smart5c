package com.nbk.smart5c.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 缺陷库表实体
 * @since 2026-05-24 19:15:51
 */
@Data
@Accessors(chain = true)
@TableName("nbk_defect_storage")
@ApiModel("缺陷库表实体")
public class DefectStorage extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "缺陷类型")
    @TableField("defect_type")
    private String defectType;

    @ApiModelProperty(value = "缺陷等级")
    @TableField("defect_level")
    private Integer defectLevel;

    @ApiModelProperty(value = "审核状态(0:未审核,1:通过,2:不通过)")
    @TableField("review_status")
    private Integer reviewStatus;

    @ApiModelProperty(value = "检测时间")
    @TableField("inspection_time")
    private LocalDateTime inspectionTime;

    @ApiModelProperty(value = "车架号")
    @TableField("locomotive_number")
    private String locomotiveNumber;

    @ApiModelProperty(value = "段id")
    @TableField("section_id")
    private Long sectionId;

    @ApiModelProperty(value = "段名")
    @TableField("section_name")
    private String sectionName;

    @ApiModelProperty(value = "线路id")
    @TableField("line_id")
    private Long lineId;

    @ApiModelProperty(value = "线路名称")
    @TableField("line_name")
    private String lineName;

    @ApiModelProperty(value = "区站id")
    @TableField("station_id")
    private Long stationId;

    @ApiModelProperty(value = "区站名")
    @TableField("station_name")
    private String stationName;

    @ApiModelProperty(value = "审核人id")
    @TableField("reviewer_id")
    private Long reviewerId;

    @ApiModelProperty(value = "审核人名称")
    @TableField("reviewer")
    private String reviewer;

    @ApiModelProperty(value = "审核文本")
    @TableField("review_text")
    private String reviewText;

    @ApiModelProperty(value = "公里标")
    @TableField("km_sign")
    private String kmSign;

    @ApiModelProperty(value = "杆号")
    @TableField("pole_no")
    private String poleNo;

    @ApiModelProperty(value = "任务Id")
    @TableField("task_id")
    private Long taskId;

    @ApiModelProperty(value = "任务名称")
    @TableField("task_name")
    private String taskName;

    @ApiModelProperty(value = "网络路径")
    @TableField("net_path")
    private String netPath;

    @ApiModelProperty(value = "磁盘相对路径")
    @TableField("image_path")
    private String imagePath;

    @ApiModelProperty(value = "标记区域")
    @TableField("marked_regions")
    private String markedRegions;

    @ApiModelProperty(value = "巡检结果id")
    @TableField("inspection_result_id")
    private Long inspectionResultId;

    @ApiModelProperty(value = "误报（0:否,1:是）")
    @TableField("misreport")
    private Integer misreport;

    @ApiModelProperty(value = "缺陷来源(0:算法,1:人工)）")
    @TableField("source")
    private Integer source;

}
