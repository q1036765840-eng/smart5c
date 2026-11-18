package com.nbk.smart5c.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author chinaxuebin@aliyun.com
 * @apiNote 用户登录
 * @since 2024-04-22 12:52:31
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户登录参数")
public class UserLoginParam {
    @NotBlank(message = "账号不能为空")
    @ApiModelProperty(value = "账号")
    private String username;
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码")
    private String password;
}