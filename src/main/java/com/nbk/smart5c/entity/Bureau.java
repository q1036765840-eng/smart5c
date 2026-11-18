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
 * @apiNote 供电部信息表实体
 * @since 2026-01-30 17:29:19
 */
@Data
@Accessors(chain = true)
@TableName("nbk_bureau")
@ApiModel("供电部信息表实体")
public class Bureau extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "供电部编码")
    @TableField("bureau_code")
    private String bureauCode;

    @ApiModelProperty(value = "供电部名称")
    @TableField("bureau_name")
    private String bureauName;

    @ApiModelProperty(value = "供电部描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "祖先")
    @TableField("ancestors")
    private String ancestors;

    @ApiModelProperty(value = "祖先")
    @TableField("path")
    private String path;

}
