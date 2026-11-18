package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 部门信息表视图对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "机构视图对象")
public class OrganVO {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "层级类型(0:局，1:供电段)")
    private Integer type;
}
