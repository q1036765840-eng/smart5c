package com.nbk.smart5c.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 系统导出信息表实体
 * @since 2026-10-27
 */
@Data
@Accessors(chain = true)
@TableName("nbk_export_info")
@ApiModel("系统导出信息表实体")
public class ExportInfo extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "导出任务编号")
    @TableField("task_no")
    private String taskNo;

    @ApiModelProperty(value = "导出所属页面")
    @TableField("source_page")
    private String sourcePage;

    @ApiModelProperty(value = "导出类型")
    @TableField("export_type")
    private String exportType;

    @ApiModelProperty(value = "导出文件名")
    @TableField("file_name")
    private String fileName;

    @ApiModelProperty(value = "数据量")
    @TableField("quantity")
    private String quantity;

    @ApiModelProperty(value = "文件大小")
    @TableField("file_size")
    private String fileSize;

    @ApiModelProperty(value = "查询参数")
    @TableField("query_param")
    private String queryParam;

    @ApiModelProperty(value = "查询接口路径")
    @TableField("query_api")
    private String queryApi;

    @ApiModelProperty(value = "导出接口路径")
    @TableField("export_api")
    private String exportApi;

    @ApiModelProperty(value = "导出状态(0:导出中,1:成功,2:失败)")
    @TableField("export_status")
    private Integer exportStatus;

}
