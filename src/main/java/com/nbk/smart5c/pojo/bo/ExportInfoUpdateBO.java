package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 系统导出信息表修改业务对象
 * @since 2026-10-27
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "系统导出信息表修改业务对象")
public class ExportInfoUpdateBO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "导出任务编号")
    private String taskNo;

    @ApiModelProperty(value = "导出所属页面")
    private String sourcePage;

    @ApiModelProperty(value = "导出类型")
    private String exportType;

    @ApiModelProperty(value = "导出文件名")
    private String fileName;

    @ApiModelProperty(value = "数据量")
    private String quantity;

    @ApiModelProperty(value = "文件大小")
    private String fileSize;

    @ApiModelProperty(value = "查询参数")
    private String queryParam;

    @ApiModelProperty(value = "查询接口路径")
    private String queryApi;

    @ApiModelProperty(value = "导出接口路径")
    private String exportApi;

    @ApiModelProperty(value = "导出状态(0:导出中,1:成功,2:失败)")
    private Integer exportStatus;
}
