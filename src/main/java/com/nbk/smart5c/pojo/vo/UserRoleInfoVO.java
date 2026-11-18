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
@ApiModel(value = "用户角色全信息视图对象")
public class UserRoleInfoVO {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "角色id")
    private Long roleId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "角色编码")
    private String roleCode;
}
