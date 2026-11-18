package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 实时推送报警消息提醒视图
 * @since 2026-01-30 17:29:19
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "实时推送报警消息提醒视图")
public class AiAlarmBO {
//title": "报警标题", "content": "报警内容", "url": "http://跳转链接"}
    @ApiModelProperty(value = "报警标题")
    private String title;

    @ApiModelProperty(value = "报警内容")
    private String content;

    @ApiModelProperty(value = "跳转链接")
    private String url;
}
