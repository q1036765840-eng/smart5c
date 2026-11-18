package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 用户信息新增业务对象
 * @since 2024-04-30 17:18:33
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户修改密码参数")
public class UserUpdatePwdBO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "旧密码", required = true)
    private String oldPassword;

    @ApiModelProperty(value = "新密码", required = true)
    private String newPassword;

    @ApiModelProperty(value = "确认密码", required = true)
    private String affirmPassword;
}
