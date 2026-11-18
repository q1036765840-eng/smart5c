package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 供电部信息表修改业务对象
 * @since 2026-01-30 17:29:19
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "供电部信息表修改业务对象")
public class BureauUpdateBO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "供电部编码")
    private String bureauCode;

    @ApiModelProperty(value = "供电部名称")
    private String bureauName;

    @ApiModelProperty(value = "供电部描述")
    private String description;

    @ApiModelProperty(value = "创建人")
    private String createdUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新人")
    private String updatedUser;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updatedTime;
}
