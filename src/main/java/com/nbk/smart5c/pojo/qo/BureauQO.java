package com.nbk.smart5c.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 供电部信息表查询对象
 * @since 2026-01-30 17:29:19
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "供电部信息表查询对象")
public class BureauQO {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "供电部编码")
    private String bureauCode;
    @ApiModelProperty(value = "供电部名称")
    private String bureauName;
    @ApiModelProperty(value = "供电部描述")
    private String description;
    @ApiModelProperty(value = "创建人")
    private String createdUser;
    @ApiModelProperty(value = "更新人")
    private String updatedUser;
}
