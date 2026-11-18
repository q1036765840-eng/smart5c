package com.nbk.smart5c.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 用户信息表实体
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@TableName("nbk_user_info")
@ApiModel("用户信息表实体")
public class UserInfo extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户名")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty(value = "密码")
    @TableField("password")
    @JsonIgnore
    private String password;

    @ApiModelProperty(value = "账号")
    @TableField("account_no")
    private String accountNo;

    @ApiModelProperty(value = "电话")
    @TableField("telephone")
    private String telephone;

    @ApiModelProperty(value = "头像链接")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty(value = "部门id")
    @TableField("department_id")
    private Long departmentId;

    @ApiModelProperty(value = "部门名称")
    @TableField(exist = false)
    private String departmentName;

    @ApiModelProperty(value = "登录错误次数（最大次数）")
    @TableField("login_times")
    private Integer loginTimes;

    @ApiModelProperty(value = "邮箱地址")
    @TableField("email")
    private String email;

    @ApiModelProperty(value = "锁定状态（0：未锁定，1：锁定）")
    @TableField("lock_status")
    private Integer lockStatus;

    @ApiModelProperty(value = "岗位(0:系统管理员,1:技术专工,2:检测员,3:只读用户)")
    @TableField("position")
    private Integer position;

    @ApiModelProperty(value = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("last_login")
    private LocalDateTime lastLogin;

    @ApiModelProperty(value = "角色名称")
    @TableField(exist = false)
    private String roleName;

    @ApiModelProperty(value = "角色id")
    @TableField(exist = false)
    private Long roleId;

}
