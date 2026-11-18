package com.nbk.smart5c.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 线路信息表查询对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "线路信息表查询对象")
public class LineQO {
    @ApiModelProperty(value = "区域id")
    private Integer sectionId;
    @ApiModelProperty(value = "线路编码")
    private String lineCode;
    @ApiModelProperty(value = "线路名称")
    private String lineName;
    @ApiModelProperty(value = "描述")
    private String description;
}
