package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 缺陷库审核业务对象
 * @since 2026-05-25 16:53:29
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "缺陷库审核业务对象")
public class DefectStorageReviewBO {

    @ApiModelProperty(value = "缺陷库主键集合")
    private List<Long> ids;

    @ApiModelProperty(value = "审核状态(0:未审核,1:通过,2:不通过)")
    private Integer reviewStatus;

    @ApiModelProperty(value = "标记区域")
    private List<DefectMarkBO> markedRegions;

    @ApiModelProperty(value = "审核文本")
    private String reviewText;
}
