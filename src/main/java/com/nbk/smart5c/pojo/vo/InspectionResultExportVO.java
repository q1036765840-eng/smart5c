package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查审核结果导出视图对象
 * @since 2026-02-03 10:47:58
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "巡查审核结果导出视图对象")
public class InspectionResultExportVO {

    @ApiModelProperty(value = "巡查id")
    private Long inspectionId;

    @ApiModelProperty(value = "巡查时间")
    private LocalDateTime inspectionTime;

    @ApiModelProperty(value = "5C装置编号")
    private String deviceCode;

    @ApiModelProperty(value = "段")
    private String sectionName;

    @ApiModelProperty(value = "线路")
    private String lineName;

    @ApiModelProperty(value = "区站")
    private String stationName;

    @ApiModelProperty(value = "支柱号")
    private String pillarNo;

    @ApiModelProperty(value = "公里标")
    private String kmSign;

    @ApiModelProperty(value = "分析人")
    private String analyzer;

    @ApiModelProperty(value = "机车号")
    private String locomotiveNumber;

    @ApiModelProperty(value = "图片是否合规")
    private Integer imageValid;

    @ApiModelProperty(value = "机车图片文件夹")
    private String locomotiveImage;

    @ApiModelProperty(value = "受电弓状态(0:异常,1:正常)")
    private Integer pantographStatus;

    @ApiModelProperty(value = "问题描述")
    private String problemDesc;

    @ApiModelProperty(value = "标记区域")
    private String markedRegions;

    @ApiModelProperty(value = "机车图片")
    private String imagePath;
}
