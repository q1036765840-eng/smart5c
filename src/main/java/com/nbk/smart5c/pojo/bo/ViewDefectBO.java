package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 查看缺陷业务对象
 * @since 2026-05-25 16:53:29
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "查看缺陷业务对象")
public class ViewDefectBO {

    @ApiModelProperty(value = "巡检结果id")
    private Long inspectionResultId;

    @ApiModelProperty(value = "巡检id")
    private Long inspectionId;

}
