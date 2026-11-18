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
 * @apiNote 角色信息表实体
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@TableName("nbk_role_info")
@ApiModel("角色信息表实体")
public class RoleInfo extends BaseEntity {

    @ApiModelProperty(value = "primary key")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "角色编码")
    @TableField("role_code")
    private String roleCode;

    @ApiModelProperty(value = "角色名称(系统管理员、技术专工、检测员、只读用户)")
    @TableField("role_name")
    private String roleName;

    @ApiModelProperty(value = "角色等级")
    @TableField("role_level")
    private Integer roleLevel;

}
