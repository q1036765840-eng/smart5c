package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 区站信息表修改业务对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "区站信息表修改业务对象")
public class StationUpdateBO {

    @ApiModelProperty(value = "primary key")
    private Long id;

    @ApiModelProperty(value = "线路id")
    private Long lineId;

    @ApiModelProperty(value = "区站编码")
    private String stationCode;

    @ApiModelProperty(value = "区站名")
    private String stationName;

    @ApiModelProperty(value = "区站描述")
    private String description;

    @ApiModelProperty(value = "创建人")
    private String createdUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "修改人")
    private String updatedUser;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedTime;
}
