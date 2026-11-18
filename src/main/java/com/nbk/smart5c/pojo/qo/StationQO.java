package com.nbk.smart5c.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 区站信息表查询对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "区站信息表查询对象")
public class StationQO {
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
    @ApiModelProperty(value = "修改人")
    private String updatedUser;
}
