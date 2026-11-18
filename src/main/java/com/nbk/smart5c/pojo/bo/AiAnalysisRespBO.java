

package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 算法分析响应参数
 * @since 2026-01-29 10:35:00
 */
@Data
@ToString
@Accessors(chain = true)
@ApiModel(value = "算法分析响应参数")
public class AiAnalysisRespBO {

    @ApiModelProperty(value = "响应数据")
    private AiAnalysisRespDataBO data;

    @ApiModelProperty(value = "是否成功")
    private Boolean success;
}
