package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 用户信息表新增业务对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户信息表新增业务对象")
public class UserInfoSaveBO {

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "账号")
    private String accountNo;

    @ApiModelProperty(value = "电话")
    private String telephone;

    @ApiModelProperty(value = "部门id")
    private Integer departmentId;

    @ApiModelProperty(value = "邮箱地址")
    private String email;
}
