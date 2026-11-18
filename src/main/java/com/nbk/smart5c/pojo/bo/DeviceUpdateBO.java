package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 设备信息表修改业务对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "设备信息表修改业务对象")
public class DeviceUpdateBO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "区站id")
    private Integer stationId;

    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    @ApiModelProperty(value = "安装日期")
    private LocalDate installationDate;

    @ApiModelProperty(value = "制造商")
    private String manufacturer;

    @ApiModelProperty(value = "杆号")
    private String pillarNo;

    @ApiModelProperty(value = "型号")
    private String model;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "上次维护日期")
    private LocalDate lastMaintenanceDate;

    @ApiModelProperty(value = "下次维护日期")
    private LocalDate nextMaintenanceDate;

    @ApiModelProperty(value = "参数")
    private String parameters;

    @ApiModelProperty(value = "创建人")
    private String createdUser;

    @ApiModelProperty(value = "更新人")
    private String updatedUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedTime;
}
