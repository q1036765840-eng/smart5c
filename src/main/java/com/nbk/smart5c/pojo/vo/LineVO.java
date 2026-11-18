package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 线路信息表视图对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "线路信息表视图对象")
public class LineVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "区域id")
    private Integer sectionId;

    @ApiModelProperty(value = "线路编码")
    private String lineCode;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "行别(0:上行,1:下行,2:单线,3:其它)")
    private Integer direction;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "创建人")
    private String createdUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新人")
    private String updatedUser;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedTime;
}
