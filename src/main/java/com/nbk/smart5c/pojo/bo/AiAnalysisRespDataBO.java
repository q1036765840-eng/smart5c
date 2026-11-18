

package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 算法分析响应参数数
 * @since 2026-01-29 10:35:00
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "算法分析响应数据")
public class AiAnalysisRespDataBO {

    @ApiModelProperty(value = "机车号")
    private String license_plate;

    @ApiModelProperty(value = "状态（正常，异常）")
    private String status;

    @ApiModelProperty(value = "是否有受电弓")
    private Boolean has_sdg;

    @ApiModelProperty(value = "图片路径")
    private String image_path;

    @ApiModelProperty(value = "图片形状")
    private AiAnalysisAnomalyDetailBO anomaly_details;
}