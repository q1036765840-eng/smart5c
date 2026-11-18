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
 * @apiNote 机务段信息表实体
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@TableName("nbk_section")
@ApiModel("机务段信息表实体")
public class Section extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "机务段编码")
    @TableField("section_code")
    private String sectionCode;

    @ApiModelProperty(value = "机务段名称")
    @TableField("section_name")
    private String sectionName;

    @ApiModelProperty(value = "机务段描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "局id")
    @TableField("bureau_id")
    private Long bureauId;
}
