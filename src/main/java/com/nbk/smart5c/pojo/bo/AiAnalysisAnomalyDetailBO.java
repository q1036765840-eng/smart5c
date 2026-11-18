

package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 算法分析响应参数数
 * @since 2026-01-29 10:35:00
 */
@Data
@ToString
@Accessors(chain = true)
@ApiModel(value = "算法分析异常明细数据")
public class AiAnalysisAnomalyDetailBO {
    private Bending bending;
    private ForeignObject foreign_object;

    //弯曲对象
    @Data
    @ToString
    @Accessors(chain = true)
    @ApiModel(value = "变曲数据")
    public static class Bending {
        @ApiModelProperty(value = "是否有异常")
        private Boolean detected;

        @ApiModelProperty(value = "坐标及置信度")
        private List<List<Object>> bboxes;

    }

    //异物对象
    @Data
    @ToString
    @Accessors(chain = true)
    @ApiModel(value = "异物数据")
    public static class ForeignObject {

        @ApiModelProperty(value = "是否有异常")
        private Boolean detected;

        @ApiModelProperty(value = "坐标及置信度")
        private List<List<Object>> bboxes;

    }
}

