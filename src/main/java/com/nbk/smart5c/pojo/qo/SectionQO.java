package com.nbk.smart5c.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 机务段信息表查询对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "机务段信息表查询对象")
public class SectionQO {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "机务段编码")
    private String sectionCode;
    @ApiModelProperty(value = "机务段名称")
    private String sectionName;
    @ApiModelProperty(value = "机务段描述")
    private String description;
    @ApiModelProperty(value = "创建人")
    private String createdUser;
    @ApiModelProperty(value = "更新人")
    private String updatedUser;
}
