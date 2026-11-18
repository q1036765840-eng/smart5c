package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 最新报警列表视图对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "最新报警列表视图对象")
public class LatestAlarmListVO {

    @ApiModelProperty(value = "分析文件夹id")
    private Long id;

    @ApiModelProperty(value = "机车号")
    private String locomotiveNumber;

    @ApiModelProperty(value = "报警时间")
    private LocalDateTime inspectionTime;

    @ApiModelProperty(value = "安装位置")
    private String fixLocation;

    @ApiModelProperty(value = "机车图")
    private String locomotiveImage;

    @ApiModelProperty(value = "分析状态")
    private Integer analysisStatus;
}
