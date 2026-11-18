package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 确认缺陷业务对象
 * @since 2026-05-25 16:53:29
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "删除某一缺陷业务对象")
public class DefectDeleteBO {

    @ApiModelProperty(value = "巡检结果id")
    private Long inspectionResultId;

    @ApiModelProperty(value = "标记区域")
    private DefectMarkBO markedRegion;
}
