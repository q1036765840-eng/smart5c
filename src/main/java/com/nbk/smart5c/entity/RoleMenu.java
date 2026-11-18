package com.nbk.smart5c.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 角色和菜单关联表实体
 * @since 2026-01-27 20:44:51
 */
@Data
@Accessors(chain = true)
@TableName("nbk_role_menu")
@ApiModel("角色和菜单关联表实体")
public class RoleMenu {

    @ApiModelProperty(value = "角色ID")
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    @ApiModelProperty(value = "菜单ID")
    @TableField("menu_id")
    private Long menuId;

}
