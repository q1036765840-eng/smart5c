package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 缺陷库表视图对象
 * @since 2026-05-19 16:16:19
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "缺陷库表视图对象")
public class DefectStorageVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "缺陷类型")
    private String defectType;

    @ApiModelProperty(value = "缺陷等级")
    private Integer defectLevel;

    @ApiModelProperty(value = "审核状态(0:未审核,1:通过,2:不通过)")
    private Integer reviewStatus;

    @ApiModelProperty(value = "检测时间")
    private LocalDateTime inspectionTime;

    @ApiModelProperty(value = "车架号")
    private String locomotiveNumber;

    @ApiModelProperty(value = "线路id")
    private Long lineId;

    @ApiModelProperty(value = "区站id")
    private Long stationId;

    @ApiModelProperty(value = "区站名")
    private String stationName;

    @ApiModelProperty(value = "审核人id")
    private Long reviewerId;

    @ApiModelProperty(value = "审核意见")
    private String reviewText;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "段名")
    private String sectionName;

    @ApiModelProperty(value = "审核人名称")
    private String reviewer;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "杆号")
    private String poleNo;

    @ApiModelProperty(value = "网络路径")
    private String netPath;

    @ApiModelProperty(value = "磁盘相对路径")
    private String imagePath;

    @ApiModelProperty(value = "图片名称")
    private String imageName;

    @ApiModelProperty(value = "标记区域")
    private String markedRegions;

    @ApiModelProperty(value = "巡检结果id")
    private Long inspectionResultId;

    @ApiModelProperty(value = "创建人")
    private String createdUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "修改人")
    private String updatedUser;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updatedTime;
}
