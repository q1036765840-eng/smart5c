package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据审核结果表视图对象
 * @since 2026-01-29 11:06:58
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "巡查数据审核结果表视图对象")
public class InspectionReviewResultVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "巡检id")
    private Long inspectionId;

    @ApiModelProperty(value = "缺陷图磁盘路径")
    private String imagePath;

    @ApiModelProperty(value = "文件网络路径")
    private String netPath;

    @ApiModelProperty(value = "缺陷描述")
    private String problemDesc;

    @ApiModelProperty(value = "受电弓状态(0:异常,1:正常)")
    private Integer pantographStatus;

    @ApiModelProperty(value = "缺陷等级")
    private Integer defectLevel;

    @ApiModelProperty(value = "图片是否合规")
    private Integer imageValid;

    @ApiModelProperty(value = "标记区域")
    private String markedRegions;

    @ApiModelProperty(value = "机车号")
    private String locomotiveNumber;

    @ApiModelProperty(value = "是否误报(0:否,1:是)")
    private Integer misreport;

    @ApiModelProperty(value = "确认状态(0:未查看,1:已查看,2:已确认)")
    private Integer confirmStatus;

    @ApiModelProperty(value = "置信度")
    private String reliability;

    @ApiModelProperty(value = "创建人")
    private String createdUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "修改人")
    private String updatedUser;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updatedTime;
}
