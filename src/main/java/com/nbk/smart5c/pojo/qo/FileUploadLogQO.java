package com.nbk.smart5c.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 文件上传记录表查询对象
 * @since 2026-04-08 15:26:45
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "文件上传日志查询对象")
public class FileUploadLogQO {
    @ApiModelProperty(value = "文件路径")
    private String folderName;
    @ApiModelProperty(value = "开始日期")
    private String startDate;
    @ApiModelProperty(value = "结束日期")
    private String endDate;
    @ApiParam(value = "页码", required = true, defaultValue = "1")
    private Integer pageNo = 1;
    @ApiParam(value = "每页条数", required = true, defaultValue = "10")
    private Integer pageSize = 10;
}
