package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据表视图对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "巡查数据表视图对象")
public class InspectionDataVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "设备id")
    private Integer deviceId;

    @ApiModelProperty(value = "机务段id")
    private Integer sectionId;

    @ApiModelProperty(value = "线路id")
    private Integer lineId;

    @ApiModelProperty(value = "区站id")
    private Integer stationId;

    @ApiModelProperty(value = "区站名")
    private String stationName;

    @ApiModelProperty(value = "杆号")
    private String poleNo;

    @ApiModelProperty(value = "巡查时间")
    private LocalDateTime inspectionTime;

    @ApiModelProperty(value = "机车号")
    private String locomotiveNumber;

    @ApiModelProperty(value = "分析状态")
    private Integer analysisStatus;

    @ApiModelProperty(value = "公里标")
    private String kmSign;

    @ApiModelProperty(value = "受电弓状态")
    private Integer pantographStatus;

    @ApiModelProperty(value = "机车图")
    private String locomotiveImage;

    @ApiModelProperty(value = "确认状态(0:未查看,1:已查看,2:已确认)")
    private Integer confirmStatus;

    @ApiModelProperty(value = "确认状态(0:未查看,1:已查看,2:已确认)")
    private String confirmStatusName;

    @ApiModelProperty(value = "创建人")
    private String createdUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新人")
    private String updatedUser;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedTime;
}
