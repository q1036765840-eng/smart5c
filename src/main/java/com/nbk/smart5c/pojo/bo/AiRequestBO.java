package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote AI请求参数对象
 * @since 2026-01-30 17:29:19
 */
@Data
@ToString
@Accessors(chain = true)
@ApiModel(value = "AI请求参数对象")
public class AiRequestBO {
    @ApiModelProperty(value = "图片路径")
    private String image;
}
