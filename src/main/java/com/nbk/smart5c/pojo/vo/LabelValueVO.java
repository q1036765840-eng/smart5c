package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 用户角色信息表视图对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "标签值视图对象")
public class LabelValueVO {

    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "值")
    private Long value;
}
