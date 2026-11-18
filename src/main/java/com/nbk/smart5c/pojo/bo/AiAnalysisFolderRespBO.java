

package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 算法分析响应参数
 * @since 2026-01-29 10:35:00
 */
@Data
@ToString
@Accessors(chain = true)
@ApiModel(value = "算法分析响应参数")
public class AiAnalysisFolderRespBO {

    @ApiModelProperty(value = "错误明细")
    private List<Object> error_details;

    @ApiModelProperty(value = "错误数量")
    private Integer errors;

    @ApiModelProperty(value = "提示消息")
    private String message;

    @ApiModelProperty(value = "图片分析结果合集")
    private List<AiAnalysisRespDataBO> results;

    @ApiModelProperty(value = "成功状态")
    private Boolean success;

    @ApiModelProperty(value = "成功数")
    private Integer successful;

    @ApiModelProperty(value = "总图片数")
    private Integer total;
}
