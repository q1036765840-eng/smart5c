package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据审核业务对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "巡查数据审核业务对象")
public class InspectionDataReviewBO {

    @ApiModelProperty(value = "巡查数据id")
    private Long inspectionId;

    @ApiModelProperty(value = "分析人id")
    private Long analyzerId;

    @ApiModelProperty(value = "机务段id")
    private Integer sectionId;

    @ApiModelProperty(value = "图片有效(0:无效,1:有效)")
    private Integer imageValid;

    @ApiModelProperty(value = "受电弓状态(正常:1,异常:2)")
    private Integer pantographStatus;

    @ApiModelProperty(value = "问题描述")
    private String problemDesc;

    @ApiModelProperty(value = "机车图")
    private String locomotiveImage;

    @ApiModelProperty(value = "标记区域")
    private List<String> markedRegions;

    @ApiModelProperty(value = "机车号")
    private String locomotiveNumber;

}
