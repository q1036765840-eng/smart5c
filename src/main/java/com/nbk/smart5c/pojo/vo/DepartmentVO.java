package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 部门信息表视图对象
 * @since 2026-05-16 17:20:28
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "部门信息表视图对象")
public class DepartmentVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "部门编码")
    private String departmentCode;

    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @ApiModelProperty(value = "父机构id")
    private Integer parentId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建人")
    private String createdUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "修改人")
    private String updatedUser;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updatedTime;

    @ApiModelProperty(value = "祖先")
    private String ancestors;

    @ApiModelProperty(value = "机构路径")
    private String path;
}
