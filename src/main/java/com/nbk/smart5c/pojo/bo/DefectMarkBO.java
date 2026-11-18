package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 缺陷标记业务对象
 * @since 2026-05-25 16:53:29
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "缺陷标记业务对象")
public class DefectMarkBO {

    @ApiModelProperty(value = "坐标")
    private String coord;

    @ApiModelProperty(value = "缺陷类型")
    private String defectType;

    @ApiModelProperty(value = "缺陷来源:0:其它未知, 1-算法, 2-人工")
    private Integer source;
}
