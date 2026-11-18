package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 菜单权限表修改业务对象
 * @since 2026-01-27 20:44:51
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "菜单权限表修改业务对象")
public class MenuUpdateBO {

    @ApiModelProperty(value = "菜单ID")
    private Long id;

    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "父菜单ID")
    private Long parentId;

    @ApiModelProperty(value = "显示顺序")
    private Integer orderNum;

    @ApiModelProperty(value = "路由地址")
    private String path;

    @ApiModelProperty(value = "组件路径")
    private String component;

    @ApiModelProperty(value = "路由参数")
    private String queryParam;

    @ApiModelProperty(value = "路由名称")
    private String routeName;

    @ApiModelProperty(value = "是否为外链（0是 1否）")
    private Integer isFrame;

    @ApiModelProperty(value = "是否缓存（0缓存 1不缓存）")
    private Integer isCache;

    @ApiModelProperty(value = "菜单类型（M目录 C菜单 F按钮）")
    private String menuType;

    @ApiModelProperty(value = "菜单状态（0显示 1隐藏）")
    private String visible;

    @ApiModelProperty(value = "菜单状态（0正常 1停用）")
    private String status;

    @ApiModelProperty(value = "权限标识")
    private String perms;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty(value = "创建者")
    private String createUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新者")
    private String updateUser;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "备注")
    private String remark;
}
