

package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 图片验证进度响应参数
 * @since 2026-01-29 10:35:00
 */
@Data
@ToString
@Accessors(chain = true)
@ApiModel(value = "图片验证进度响应参数")
public class ImageValidateProgressBO {

    @ApiModelProperty(value = "是否删除")
    private Integer deleted;

    @ApiModelProperty(value = "已用时间")
    private Double elapsed_time;

    @ApiModelProperty(value = "错误数")
    private Integer errors;

    @ApiModelProperty(value = "验证的总文件夹")
    private String folder_path;

    @ApiModelProperty(value = "消息")
    private String message;

    @ApiModelProperty(value = "验证%")
    private Double percent;

    @ApiModelProperty(value = "已处理数")
    private Integer processed;

    @ApiModelProperty(value = "总数")
    private Integer total;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "成功状态")
    private Boolean success;
}
