package com.nbk.smart5c.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nbk.smart5c.pojo.bo.DefectMarkBO;
import com.nbk.smart5c.pojo.vo.ImageReviewInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据表实体
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@TableName("nbk_inspection_data")
@ApiModel("巡查数据表实体")
public class InspectionData extends BaseEntity {

    public static final Integer ANALYSIS_STATUS_WAIT_A = 0; //待分析
    public static final Integer ANALYSIS_STATUS_WAIT_R = 1; //待审批
    public static final Integer ANALYSIS_STATUS_END = 2; //已审批
    public static final Integer PANTOGRAPH_STATUS_UNKNOWN = 0; //受电工未分析
    public static final Integer PANTOGRAPH_STATUS_RIGHT = 1; //受电工正常
    public static final Integer PANTOGRAPH_STATUS_ERROR = 2; //受电工异常
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "设备id")
    @TableField("device_id")
    private Long deviceId;

    @ApiModelProperty(value = "设备编码")
    @TableField(exist = false)
    private String deviceCode;

    @ApiModelProperty(value = "支柱号")
    @TableField(exist = false)
    private String pillarNo;

    @ApiModelProperty(value = "机务段id")
    @TableField("section_id")
    private Long sectionId;

    @ApiModelProperty(value = "机务段名称")
    @TableField(exist = false)
    private String sectionName;

    @ApiModelProperty(value = "线路id")
    @TableField("line_id")
    private Long lineId;

    @ApiModelProperty(value = "线路名")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty(value = "区站id")
    @TableField("station_id")
    private Long stationId;

    @ApiModelProperty(value = "区站名")
    @TableField(exist = false)
    private String stationName;

    @ApiModelProperty(value = "缺陷类型")
    @TableField(exist = false)
    private String defectType;

    @ApiModelProperty(value = "巡查时间")
    @TableField("inspection_time")
    private LocalDateTime inspectionTime;

    @ApiModelProperty(value = "机车号")
    @TableField("locomotive_number")
    private String locomotiveNumber;

    @ApiModelProperty(value = "分析状态(未分析:0,待审批:1,已审批:2)")
    @TableField("analysis_status")
    private Integer analysisStatus;

    @ApiModelProperty(value = "公里标")
    @TableField("km_sign")
    private String kmSign;

    @ApiModelProperty(value = "受电弓状态")
    @TableField("pantograph_status")
    private Integer pantographStatus;

    //存放的是图片目录
    @ApiModelProperty(value = "机车图")
    @TableField("locomotive_image")
    private String locomotiveImage;

    //存放的是图片目录
    @ApiModelProperty(value = "速度")
    @TableField("speed")
    private Double speed;

    //存放的是图片目录
    @ApiModelProperty(value = "任务id")
    @TableField("task_id")
    private Long taskId;

    @ApiModelProperty(value = "是否误报")
    @TableField("misreport")
    private Integer misreport;

    @ApiModelProperty(value = "任务名")
    @TableField(exist = false)
    private String taskName;

    //目录图片数
    @ApiModelProperty(value = "图片数")
    @TableField("image_num")
    private Integer imageNum;

    //确认状态
    @ApiModelProperty(value = "确认状态(0:未查看,1:已查看,2:已确认,3:误报)")
    @TableField("confirm_status")
    private Integer confirmStatus;

    @ApiModelProperty(value = "标记区域")
    @TableField(exist = false)
    private List<DefectMarkBO> markedRegions;

    @ApiModelProperty(value = "图片是否有效")
    @TableField(exist = false)
    private Integer imageValid;

    @ApiModelProperty(value = "缺陷描述")
    @TableField(exist = false)
    private String problemDesc;

    @ApiModelProperty(value = "分析人")
    @TableField(exist = false)
    private String analyzer;

    @ApiModelProperty(value = "图片链接前缀")
    @TableField(exist = false)
    private String imageUrl;

    @ApiModelProperty(value = "图片文件列表")
    @TableField(exist = false)
    private List<ImageReviewInfoVO> imageFileList;

}
