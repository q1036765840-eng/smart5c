package com.nbk.smart5c.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 用户角色信息表查询对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户角色信息表查询对象")
public class UserRoleQO {
    @ApiModelProperty(value = "主键")
    private Integer id;
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "角色id")
    private Integer roleId;
    @ApiModelProperty(value = "创建人")
    private String createdUser;
    @ApiModelProperty(value = "修改人")
    private String updatedUser;
}
