package com.nbk.smart5c.entity;

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
 * @since 2026-04-07 14:57:09
 */
@Data
@Accessors(chain = true)
@TableName("nbk_task")
@ApiModel("任务表实体")
public class Task extends BaseEntity {

    public static final int TASK_STATUS_CREATING = 0; //任务状态(0:创建中)
    public static final int TASK_STATUS_WAIT_ANALYSIS = 1; //任务状态(1:待分析)
    public static final int TASK_STATUS_ANALYSISING = 2; //任务状态(2:分析中)
    public static final int TASK_STATUS_PAUSE = 3; //任务状态(3:暂停)
    public static final int TASK_STATUS_FINISH_ANALYSIS = 4; //任务状态(4:分析完成)
    public static final int TASK_STATUS_WAIT_QUEUE = 9; //任务状态(9:等待中，已加入等待队列)

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

    @ApiModelProperty(value = "任务状态(0:创建中,1:待分析,2:分析中,3:暂停,4:分析完成,9:等待中)")
    @TableField("task_status")
    private Integer taskStatus;

    @ApiModelProperty(value = "分析进度")
    @TableField("analysis_progress")
    private String analysisProgress;

    @ApiModelProperty(value = "文件大小")
    @TableField("file_size")
    private Double fileSize;

    @ApiModelProperty(value = "扩展名")
    @TableField("ext_name")
    private String extName;

    @ApiModelProperty(value = "文件数量")
    @TableField("file_nums")
    private Integer fileNums;

    @ApiModelProperty(value = "已导入文件数量")
    @TableField("import_nums")
    private Integer importNums;

    @ApiModelProperty(value = "分析时长(秒数)")
    @TableField("analyzed_duration")
    private Long analyzedDuration;

    @ApiModelProperty(value = "已分析文件夹数")
    @TableField("analyzed_folders")
    private Integer analyzedFolders;

    @ApiModelProperty(value = "数据类型(0:图片,1:视频)")
    @TableField("data_type")
    private Integer dataType;

    @ApiModelProperty(value = "项点")
    @TableField("item_points")
    private String itemPoints;

    @ApiModelProperty(value = "AI报警数")
    @TableField("ai_alarms")
    private Integer aiAlarms;

    @ApiModelProperty(value = "复核数")
    @TableField("review_nums")
    private Integer reviewNums;

    @ApiModelProperty(value = "检出率(检出真实缺陷数/真实缺陷总数)")
    @TableField("checkout_rate")
    private BigDecimal checkoutRate;

    @ApiModelProperty(value = "提报率(智能识别疑似缺陷数/总检测图片数)")
    @TableField("reporting_rate")
    private BigDecimal reportingRate;

    @ApiModelProperty(value = "准确率")
    @TableField("hit_rate")
    private BigDecimal hitRate;

    @ApiModelProperty(value = "开始分析时间")
    @TableField("start_check_time")
    private LocalDateTime startCheckTime;

    @ApiModelProperty(value = "结束分析时间")
    @TableField("end_check_time")
    private LocalDateTime endCheckTime;

    @ApiModelProperty(value = "暂停时间")
    @TableField("pause_time")
    private LocalDateTime pauseTime;

    @ApiModelProperty(value = "截止时间")
    @TableField("deadline")
    private LocalDateTime deadline;

    @ApiModelProperty(value = "是否实时任务(0:否,1:是)")
    @TableField("living")
    private Integer living;

    public Integer getReviewStatus() {
        return reviewNums == null ? 0 : reviewNums < fileNums ? 0 : 1;
    }
}
