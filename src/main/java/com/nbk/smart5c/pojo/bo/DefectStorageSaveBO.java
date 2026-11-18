package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 缺陷库表新增业务对象
 * @since 2026-05-25 16:53:29
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "缺陷库表新增业务对象")
public class DefectStorageSaveBO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "缺陷类型")
    private String defectType;

    @ApiModelProperty(value = "缺陷等级")
    private Integer defectLevel;

    @ApiModelProperty(value = "审核状态(0:未审核,1:已审核)")
    private Integer reviewStatus;

    @ApiModelProperty(value = "检测时间")
    private LocalDateTime inspectionTime;

    @ApiModelProperty(value = "车架号")
    private String locomotiveNumber;

    @ApiModelProperty(value = "段id")
    private Long sectionId;

    @ApiModelProperty(value = "段名")
    private String sectionName;

    @ApiModelProperty(value = "线路id")
    private Long lineId;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "区站id")
    private Long stationId;

    @ApiModelProperty(value = "区站名")
    private String stationName;

    @ApiModelProperty(value = "审核人id")
    private Long reviewerId;

    @ApiModelProperty(value = "审核人名称")
    private String reviewer;

    @ApiModelProperty(value = "审核文本")
    private String reviewText;

    @ApiModelProperty(value = "公里标")
    private String kmSign;

    @ApiModelProperty(value = "杆号")
    private String poleNo;

    @ApiModelProperty(value = "任务id")
    private Long taskId;

    @ApiModelProperty(value = "任务名")
    private String taskName;

    @ApiModelProperty(value = "创建人")
    private Long createdUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "修改人")
    private Long updatedUser;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updatedTime;
}
