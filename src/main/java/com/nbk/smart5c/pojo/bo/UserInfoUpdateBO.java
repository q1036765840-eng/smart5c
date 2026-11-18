package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 用户信息表修改业务对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户信息表修改业务对象")
public class UserInfoUpdateBO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "账号")
    private String accountNo;

    @ApiModelProperty(value = "电话")
    private String telephone;

    @ApiModelProperty(value = "头像链接")
    private String avatar;

    @ApiModelProperty(value = "部门id")
    private Integer departmentId;

    @ApiModelProperty(value = "登录错误次数（最大次数）")
    private Integer loginTimes;

    @ApiModelProperty(value = "邮箱地址")
    private String email;

    @ApiModelProperty(value = "锁定状态（0：未锁定，1：锁定）")
    private Integer lockStatus;

    @ApiModelProperty(value = "角色id")
    private Long roleId;
}
