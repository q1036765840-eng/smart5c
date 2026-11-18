
package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 人工审核参数
 * @since 2026-01-29 10:35:00
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "人工审核参数")
public class ManualReviewParamBO {

    @ApiModelProperty(value = "巡查id列表")
    private List<Long> idList;
}
