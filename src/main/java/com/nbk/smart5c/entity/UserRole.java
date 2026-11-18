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
 * @apiNote 用户角色信息表实体
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@TableName("nbk_user_role")
@ApiModel("用户角色信息表实体")
public class UserRole extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "角色id")
    @TableField("role_id")
    private Long roleId;

}
