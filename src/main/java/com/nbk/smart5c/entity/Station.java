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
 * @apiNote 区站信息表实体
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@TableName("nbk_station")
@ApiModel("区站信息表实体")
public class Station extends BaseEntity {

    @ApiModelProperty(value = "primary key")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "线路id")
    @TableField("line_id")
    private Long lineId;

    @ApiModelProperty(value = "区站编码")
    @TableField("station_code")
    private String stationCode;

    @ApiModelProperty(value = "区站名")
    @TableField("station_name")
    private String stationName;

    @ApiModelProperty(value = "区站描述")
    @TableField("description")
    private String description;

}
