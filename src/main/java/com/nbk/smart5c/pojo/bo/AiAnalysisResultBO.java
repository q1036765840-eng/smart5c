package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 分析结果表新增业务对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "分析结果表新增业务对象")
public class AiAnalysisResultBO {

    @ApiModelProperty(value = "车牌")
    private String licensePlate;

    @ApiModelProperty(value = "分析状态")
    private String status;

    @ApiModelProperty(value = "异常细节")
    private String anomalyDetails;

    @ApiModelProperty(value = "图片形状")
    private List<Integer> imageShape;
}
