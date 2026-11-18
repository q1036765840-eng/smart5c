package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 角色和菜单关联表视图对象
 * @since 2026-01-27 20:44:51
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "角色和菜单关联表视图对象")
public class RoleMenuVO {

    @ApiModelProperty(value = "角色ID")
    private Long roleId;

    @ApiModelProperty(value = "菜单ID")
    private Long menuId;
}
