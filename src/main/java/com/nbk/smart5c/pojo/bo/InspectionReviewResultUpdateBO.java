package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据审核结果表修改业务对象
 * @since 2026-01-29 10:35:00
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "巡查数据审核结果表修改业务对象")
public class InspectionReviewResultUpdateBO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "巡检id")
    private Long inspectionId;

    @ApiModelProperty(value = "缺陷图路径")
    private String imagePath;

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
}
