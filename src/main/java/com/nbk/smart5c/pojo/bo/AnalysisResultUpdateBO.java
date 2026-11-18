package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 分析结果表修改业务对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "分析结果表修改业务对象")
public class AnalysisResultUpdateBO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "巡检id")
    private Long inspectionId;

    @ApiModelProperty(value = "缺陷图路径")
    private String imagePath;

    @ApiModelProperty(value = "缺陷描述")
    private String problemDesc;

    @ApiModelProperty(value = "分析结果")
    private String analysisResult;

    @ApiModelProperty(value = "受电工状态(0:异常,1:正常)")
    private Integer pantographStatus;

    @ApiModelProperty(value = "严重性等级")
    private Integer severityLevel;

    @ApiModelProperty(value = "图片是否合规")
    private Integer imageValid;

    @ApiModelProperty(value = "标记区域")
    private String markedRegions;

    @ApiModelProperty(value = "标记区域")
    private String tags;

    @ApiModelProperty(value = "创建人")
    private String createdUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "修改人")
    private String updatedUser;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updatedTime;
}
