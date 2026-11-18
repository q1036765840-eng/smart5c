package com.nbk.smart5c.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 菜单权限表实体
 * @since 2026-01-27 20:44:51
 */
@Data
@Accessors(chain = true)
@TableName("nbk_menu")
@ApiModel("菜单权限表实体")
public class Menu extends BaseEntity {

    @ApiModelProperty(value = "菜单ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "菜单名称")
    @TableField("menu_name")
    private String menuName;

    @ApiModelProperty(value = "父菜单ID")
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty(value = "显示顺序")
    @TableField("order_num")
    private Integer orderNum;

    @ApiModelProperty(value = "路由地址")
    @TableField("path")
    private String path;

    @ApiModelProperty(value = "组件路径")
    @TableField("component")
    private String component;

    @ApiModelProperty(value = "路由参数")
    @TableField("query_param")
    private String queryParam;

    @ApiModelProperty(value = "路由名称")
    @TableField("route_name")
    private String routeName;

    @ApiModelProperty(value = "是否为外链（0是 1否）")
    @TableField("is_frame")
    private Integer isFrame;

    @ApiModelProperty(value = "是否缓存（0缓存 1不缓存）")
    @TableField("is_cache")
    private Integer isCache;

    @ApiModelProperty(value = "菜单类型（M目录 C菜单 F按钮）")
    @TableField("menu_type")
    private String menuType;

    @ApiModelProperty(value = "菜单状态（0显示 1隐藏）")
    @TableField("visible")
    private String visible;

    @ApiModelProperty(value = "菜单状态（0正常 1停用）")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "权限标识")
    @TableField("perms")
    private String perms;

    @ApiModelProperty(value = "菜单图标")
    @TableField("icon")
    private String icon;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "子列表")
    @TableField(exist = false)
    private List<Menu> children;
}
