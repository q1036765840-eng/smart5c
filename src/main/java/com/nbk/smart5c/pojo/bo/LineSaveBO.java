package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 线路信息表新增业务对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "线路信息表新增业务对象")
public class LineSaveBO {

    @ApiModelProperty(value = "区域id")
    private Integer sectionId;

    @ApiModelProperty(value = "线路编码")
    private String lineCode;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "行别(0:上行,1:下行,2:单线,3:其它)")
    private Integer direction;
}
