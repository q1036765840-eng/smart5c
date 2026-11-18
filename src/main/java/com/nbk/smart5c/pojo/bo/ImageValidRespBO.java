

package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 图片有效验证响应参数
 * @since 2026-01-29 10:35:00
 */
@Data
@ToString
@Accessors(chain = true)
@ApiModel(value = "图片有效验证响应参数")
public class ImageValidRespBO {

    @ApiModelProperty(value = "是否有效(1:有效,0:无效")
    private Integer data;

    @ApiModelProperty(value = "是否成功")
    private Boolean success;
}
