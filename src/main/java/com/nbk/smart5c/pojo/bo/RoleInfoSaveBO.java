package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 角色信息表新增业务对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "角色信息表新增业务对象")
public class RoleInfoSaveBO {

    @ApiModelProperty(value = "primary key")
    private Integer id;

    @ApiModelProperty(value = "角色编码")
    private String roleCode;

    @ApiModelProperty(value = "角色名称(系统管理员、技术专工、检测员、只读用户)")
    private String roleName;

    @ApiModelProperty(value = "角色等级")
    private Integer roleLevel;

    @ApiModelProperty(value = "创建人")
    private String createdUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "修改人")
    private String updatedUser;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updatedTime;
}
