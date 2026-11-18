package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 今日检测数据视图对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "今日检测数据视图对象")
public class TodayCheckDataVO {

    @ApiModelProperty(value = "机车数")
    private Long locomotiveNum;

    @ApiModelProperty(value = "图片处理数")
    private Long pictures;

    @ApiModelProperty(value = "异常文件夹数")
    private Long detectRate;
}
