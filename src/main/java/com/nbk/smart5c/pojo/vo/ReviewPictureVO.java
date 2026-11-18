package com.nbk.smart5c.pojo.vo;

import com.nbk.smart5c.pojo.bo.DefectMarkBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 复核图片定位视图对象
 * @since 2026-05-19 16:16:19
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "复核图片定位视图对象")
public class ReviewPictureVO {

    @ApiModelProperty(value = "巡查结果ID")
    private Long inspectionResultId;

    @ApiModelProperty(value = "巡查ID")
    private Long inspectionId;

    @ApiModelProperty(value = "段")
    private String sectionName;

    @ApiModelProperty(value = "线路")
    private String lineName;

    @ApiModelProperty(value = "区站")
    private String stationName;

    @ApiModelProperty(value = "支柱号")
    private String poleNo;

    @ApiModelProperty(value = "公里标")
    private String kmSign;

    @ApiModelProperty(value = "检测时间")
    private LocalDateTime inspectionTime;

    @ApiModelProperty(value = "受电工状态")
    private Integer pantographStatus;

    @ApiModelProperty(value = "confirmStatus(0:未查看,1:已查看,2:已确认)")
    private Integer confirmStatus;

    @ApiModelProperty(value = "confirmStatus(0:未查看,1:已查看,2:已确认)")
    private String confirmStatusName;

    @ApiModelProperty(value = "图片路径")
    private String imagePath;

    @ApiModelProperty(value = "图片网络路径")
    private String netPath;

    @ApiModelProperty(value = "文件名")
    private String imageName;

    @ApiModelProperty(value = "标记区域")
    private String markedRegions;

    @ApiModelProperty(value = "问题描述")
    private String problemDesc;

    @ApiModelProperty(value = "标记区域列表")
    private List<DefectMarkBO> markedRegionList;
}
