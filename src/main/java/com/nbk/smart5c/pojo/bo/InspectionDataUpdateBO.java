package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据表修改业务对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "巡查数据表修改业务对象")
public class InspectionDataUpdateBO {

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

    @ApiModelProperty(value = "巡查时间")
    private LocalDateTime inspectionTime;

    @ApiModelProperty(value = "机车号")
    private String locomotiveNumber;

    @ApiModelProperty(value = "分析状态")
    private Integer analysisStatus;

    @ApiModelProperty(value = "公里标")
    private String kmSign;

    @ApiModelProperty(value = "受电弓状态")
    private String pantographStatus;

    @ApiModelProperty(value = "机车图")
    private String locomotiveImage;

    @ApiModelProperty(value = "创建人")
    private String createdUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新人")
    private String updatedUser;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedTime;
}
