package com.nbk.smart5c.mapper;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务表实体
 * @since 2026-04-07 16:41:49
 */
@Data
@Accessors(chain = true)
@TableName("nbk_task")
@ApiModel("任务表实体")
public class Task {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务名")
    @TableField("task_name")
    private String taskName;

    @ApiModelProperty(value = "任务文件夹")
    @TableField("task_folder")
    private String taskFolder;

    @ApiModelProperty(value = "0:低,1:中,2:高")
    @TableField("priority")
    private Integer priority;

    @ApiModelProperty(value = "任务状态(0:待分析,1:分析中,2:暂停,分析完成)")
    @TableField("task_status")
    private Integer taskStatus;

    @ApiModelProperty(value = "分析进度")
    @TableField("analysis_progress")
    private BigDecimal analysisProgress;

    @ApiModelProperty(value = "是否入库(0:未入库,1:已入库)")
    @TableField("in_storage")
    private Integer inStorage;

    @ApiModelProperty(value = "文件大小")
    @TableField("file_size")
    private Double fileSize;

    @ApiModelProperty(value = "扩展名")
    @TableField("ext_name")
    private String extName;

    @ApiModelProperty(value = "文件数量")
    @TableField("file_nums")
    private String fileNums;

    @ApiModelProperty(value = "已导入文件数量")
    @TableField("import_nums")
    private Integer importNums;

    @ApiModelProperty(value = "分析时长(秒数)")
    @TableField("analyzed_duration")
    private Double analyzedDuration;

    @ApiModelProperty(value = "已分析图片数")
    @TableField("analyzed_pictures")
    private Integer analyzedPictures;

    @ApiModelProperty(value = "数据类型(0:图片,1:视频)")
    @TableField("data_type")
    private Integer dataType;

    @ApiModelProperty(value = "项点")
    @TableField("item_points")
    private String itemPoints;

    @ApiModelProperty(value = "AI报警数")
    @TableField("ai_alarms")
    private Integer aiAlarms;

    @ApiModelProperty(value = "人工审核进度")
    @TableField("manual_progress")
    private BigDecimal manualProgress;

    @ApiModelProperty(value = "检出率(检出真实缺陷数/真实缺陷总数)")
    @TableField("checkout_rate")
    private BigDecimal checkoutRate;

    @ApiModelProperty(value = "提报率(智能识别疑似缺陷数/总检测图片数)")
    @TableField("reporting_rate")
    private BigDecimal reportingRate;

    @ApiModelProperty(value = "准确率")
    @TableField("hit_rate")
    private BigDecimal hitRate;

    @ApiModelProperty(value = "创建人")
    @TableField("created_user")
    private String createdUser;

    @ApiModelProperty(value = "创建时间")
    @TableField("created_time")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "修改人")
    @TableField("updated_user")
    private String updatedUser;

    @ApiModelProperty(value = "修改时间")
    @TableField("updated_time")
    private LocalDateTime updatedTime;

}
