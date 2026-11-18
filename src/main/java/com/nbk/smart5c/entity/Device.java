package com.nbk.smart5c.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 设备信息表实体
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@TableName("nbk_device")
@ApiModel("设备信息表实体")
public class Device extends BaseEntity {

    public static final Integer DEVICE_STATUS_STOP = 0; //停用
    public static final Integer DEVICE_STATUS_RUN = 1; //运行
    public static final Integer DEVICE_STATUS_REPAIR = 2; //维修

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "区站id")
    @TableField("station_id")
    private Long stationId;

    @ApiModelProperty(value = "设备编号")
    @TableField("device_code")
    private String deviceCode;

    @ApiModelProperty(value = "设备类型")
    @TableField("device_type")
    private String deviceType;

    @ApiModelProperty(value = "安装日期")
    @TableField("installation_date")
    private LocalDate installationDate;

    @ApiModelProperty(value = "制造商")
    @TableField("manufacturer")
    private String manufacturer;

    @ApiModelProperty(value = "杆号")
    @TableField("pillar_no")
    private String pillarNo;

    @ApiModelProperty(value = "公里标")
    @TableField("km_sign")
    private String kmSign;

    @ApiModelProperty(value = "型号")
    @TableField("model")
    private String model;

    @ApiModelProperty(value = "状态")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "上次维护日期")
    @TableField("last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @ApiModelProperty(value = "下次维护日期")
    @TableField("next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    @ApiModelProperty(value = "参数")
    @TableField("parameters")
    private String parameters;

    @ApiModelProperty(value = "安装位置")
    @TableField("fix_location")
    private String fixLocation;
}
