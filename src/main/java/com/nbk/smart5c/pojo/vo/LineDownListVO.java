package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 线路信息表视图对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "线路下拉列表视图对象")
public class LineDownListVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "线路名称")
    private String lineName;
}
