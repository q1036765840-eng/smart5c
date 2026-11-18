package com.nbk.smart5c.pojo.qo;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 用户信息表查询对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户信息表查询对象")
public class UserInfoQO {
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "账号")
    private String accountNo;
    @ApiModelProperty(value = "电话")
    private String telephone;
    @ApiModelProperty(value = "部门id")
    private Integer departmentId;
    @ApiModelProperty(value = "锁定状态（0：未锁定，1：锁定）")
    private Integer lockStatus;
    @ApiModelProperty(value = "角色id")
    private Integer roleId;
    @ApiModelProperty(name = "pageNo", value = "页号", required = true)
    private Integer pageNo;
    @ApiModelProperty(name = "pageSize", value = "页大小", required = true)
    private Integer pageSize;

    public Integer getPageNo() {
        if (ObjectUtil.isNull(pageNo)) {
            return 1;
        }
        return pageNo;
    }

    public Integer getPageSize() {
        if (ObjectUtil.isNull(pageSize)) {
            return 10;
        }
        return pageSize;
    }
}
