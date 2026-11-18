package com.nbk.smart5c.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 角色信息表查询对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "角色信息表查询对象")
public class RoleInfoQO {
    @ApiModelProperty(value = "primary key")
    private Integer id;
    @ApiModelProperty(value = "角色编码")
    private String roleCode;
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    @ApiModelProperty(value = "角色等级")
    private Integer roleLevel;

}
