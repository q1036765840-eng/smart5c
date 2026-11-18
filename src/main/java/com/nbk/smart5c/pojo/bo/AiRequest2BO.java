package com.nbk.smart5c.pojo.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote AI请求参数版本2对象
 * @since 2026-01-30 17:29:19
 */
@Data
@ToString
@Accessors(chain = true)
@ApiModel(value = "AI请求参数版本2对象")
public class AiRequest2BO {
    @ApiModelProperty(value = "文件夹路径")
    private String folder_path;
    @ApiModelProperty(value = "是否删除")
    @JsonProperty("is_delete")
    private boolean is_delete;
    @ApiModelProperty(value = "配置类型")
    @JsonProperty("config_type")
    private String config_type;
}
