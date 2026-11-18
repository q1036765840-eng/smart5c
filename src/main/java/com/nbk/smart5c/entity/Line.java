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
 * @apiNote 线路信息表实体
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@TableName("nbk_line")
@ApiModel("线路信息表实体")
public class Line extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "区域id")
    @TableField("section_id")
    private Long sectionId;

    @ApiModelProperty(value = "线路编码")
    @TableField("line_code")
    private String lineCode;

    @ApiModelProperty(value = "线路名称")
    @TableField("line_name")
    private String lineName;

    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "行别(0:上行,1:下行,2:单线,3:其它)")
    @TableField("direction")
    private Integer direction;

}
