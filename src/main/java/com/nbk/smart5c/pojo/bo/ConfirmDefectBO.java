package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 确认缺陷业务对象
 * @since 2026-05-25 16:53:29
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "确认缺陷业务对象")
public class ConfirmDefectBO {

    @ApiModelProperty(value = "巡检结果id")
    private Long inspectionResultId;

    @ApiModelProperty(value = "巡检id")
    private Long inspectionId;

    @ApiModelProperty(value = "确认状态(0:未查看,1:已查看,2:已确认)")
    private Integer confirmStatus;

    @ApiModelProperty(value = "受电弓状态(正常:1,异常:2)")
    private String pantographStatus;

    @ApiModelProperty(value = "问题描述")
    private String problemDesc;

    @ApiModelProperty(value = "机车图")
    private String locomotiveImage;

    @ApiModelProperty(value = "标记区域")
    private List<DefectMarkBO> markedRegions;

    @ApiModelProperty(value = "机车号")
    private String locomotiveNumber;
}
