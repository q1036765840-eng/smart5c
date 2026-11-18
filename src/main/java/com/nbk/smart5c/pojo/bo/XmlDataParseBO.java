package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote XML解析数据对象
 * @since 2026-01-29 10:35:00
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "XML解析数据对象")
public class XmlDataParseBO {

    @ApiModelProperty(value = "机车编号")
    private String locomotiveNumber;

    @ApiModelProperty(value = "巡检id")
    private LocalDateTime inspectionTime;

    @ApiModelProperty(value = "安装位置")
    private String fixLocation;

    @ApiModelProperty(value = "段名称")
    private String sectionName;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "区站名")
    private String stationName;

    @ApiModelProperty(value = "支柱号")
    private String poleNo;

    @ApiModelProperty(value = "公里标")
    private String kmSign;

    @ApiModelProperty(value = "行别(0:上行,1:下行,2:单线,3:其它)")
    private Integer direction;

    @ApiModelProperty(value = "行驶速度")
    private Double speed;
}
