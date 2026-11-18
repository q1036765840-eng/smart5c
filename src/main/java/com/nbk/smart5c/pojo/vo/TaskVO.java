package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务表视图对象
 * @since 2026-04-07 16:41:49
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "任务表视图对象")
public class TaskVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "任务名")
    private String taskName;

    @ApiModelProperty(value = "任务文件夹")
    private String taskFolder;

    @ApiModelProperty(value = "0:低,1:中,2:高")
    private Integer priority;

    @ApiModelProperty(value = "任务状态(0:创建中,1:待分析,2:分析中,3:暂停,4:分析完成)")
    private Integer taskStatus;

    @ApiModelProperty(value = "任务状态(0:创建中,1:待分析,2:分析中,3:暂停,4:分析完成,9:等待中)")
    private String taskStatusText;

    @ApiModelProperty(value = "分析进度")
    private String analysisProgress;

    @ApiModelProperty(value = "复核进度")
    private String reviewProgress;

    @ApiModelProperty(value = "是否入库(0:未入库,1:已入库)")
    private Integer inStorage;

    @ApiModelProperty(value = "文件大小")
    private Double fileSize;

    @ApiModelProperty(value = "扩展名")
    private String extName;

    @ApiModelProperty(value = "文件数量")
    private Integer fileNums;

    @ApiModelProperty(value = "已导入文件数量")
    private Integer importNums;

    @ApiModelProperty(value = "分析时长(秒数)")
    private Double analyzedDuration;

    @ApiModelProperty(value = "已分析文件数")
    private Integer analyzedFolders;

    @ApiModelProperty(value = "数据类型(0:图片,1:视频)")
    private Integer dataType;

    @ApiModelProperty(value = "项点")
    private String itemPoints;

    @ApiModelProperty(value = "AI报警数")
    private Integer aiAlarms;

    @ApiModelProperty(value = "复核数量")
    private Integer reviewNums;

    @ApiModelProperty(value = "检出率(检出真实缺陷数/真实缺陷总数)")
    private BigDecimal checkoutRate;

    @ApiModelProperty(value = "提报率(智能识别疑似缺陷数/总检测图片数)")
    private BigDecimal reportingRate;

    @ApiModelProperty(value = "准确率")
    private BigDecimal hitRate;

    @ApiModelProperty(value = "截止时间")
    private LocalDateTime deadline;

    @ApiModelProperty(value = "开始分析时间")
    private LocalDateTime startCheckTime;

    @ApiModelProperty(value = "结束分析时间")
    private LocalDateTime endCheckTime;

    @ApiModelProperty(value = "暂停时间")
    private LocalDateTime pauseTime;

    @ApiModelProperty(value = "是否超时")
    private Boolean timeout = false;

    @ApiModelProperty(value = "识别缺陷数")
    private Long checkDefectNum;

    @ApiModelProperty(value = "确认缺陷数")
    private Long confirmDefectNum;
}
