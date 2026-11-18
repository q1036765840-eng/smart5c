package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 部门层级树视图对象
 * @since 2026-05-08 15:33:50
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "部门层级树视图对象")
public class DepartTreeVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @ApiModelProperty(value = "父id")
    private Long parentId;

    @ApiModelProperty(value = "子部门列表")
    private List<DepartTreeVO> childList;
}
