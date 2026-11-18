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
 * @apiNote 部门信息表实体
 * @since 2026-05-16 17:20:28
 */
@Data
@Accessors(chain = true)
@TableName("nbk_department")
@ApiModel("部门信息表实体")
public class Department extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "部门编码")
    @TableField("department_code")
    private String departmentCode;

    @ApiModelProperty(value = "部门名称")
    @TableField("department_name")
    private String departmentName;

    @ApiModelProperty(value = "父机构id")
    @TableField("parent_id")
    private Integer parentId;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "祖先")
    @TableField("ancestors")
    private String ancestors;

    @ApiModelProperty(value = "机构路径")
    @TableField("path")
    private String path;

}
