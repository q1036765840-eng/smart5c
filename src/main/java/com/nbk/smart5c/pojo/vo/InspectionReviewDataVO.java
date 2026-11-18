package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据审核对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "巡查数据审核对象")
public class InspectionReviewDataVO {

    @ApiModelProperty(value = "巡查数据id")
    private Long id;

    @ApiModelProperty(value = "设备id")
    private Integer deviceId;

    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "安装位置")
    private String fixLocation;

    @ApiModelProperty(value = "线路id")
    private Integer lineId;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "区站id")
    private Integer stationId;

    @ApiModelProperty(value = "区站名")
    private String stationName;

    @ApiModelProperty(value = "支柱号")
    private String pillarNo;

    @ApiModelProperty(value = "公里标")
    private String kmSign;

    @ApiModelProperty(value = "机车号")
    private String locomotiveNumber;

    @ApiModelProperty(value = "受电弓状态")
    private String pantographStatus;

    @ApiModelProperty(value = "机车图片")
    private String locomotiveImage;

}
