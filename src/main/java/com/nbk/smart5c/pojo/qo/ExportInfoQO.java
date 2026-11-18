package com.nbk.smart5c.pojo.qo;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 系统导出信息表查询对象
 * @since 2026-10-27
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "系统导出信息表查询对象")
public class ExportInfoQO {
    @ApiModelProperty(value = "导出任务编号")
    private String taskNo;
    @ApiModelProperty(value = "导出所属页面")
    private String sourcePage;
    @ApiModelProperty(value = "导出类型")
    private String exportType;
    @ApiModelProperty(value = "导出文件名")
    private String fileName;
    @ApiModelProperty(value = "导出状态(0:导出中,1:成功,2:失败)")
    private Integer exportStatus;
    @ApiModelProperty(value = "创建人")
    private String createdUser;
    @ApiModelProperty(name = "startTime", value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @ApiModelProperty(name = "endTime", value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    @ApiModelProperty(name = "pageNo", value = "页号", required = true)
    private Integer pageNo;
    @ApiModelProperty(name = "pageSize", value = "页大小", required = true)
    private Integer pageSize;

    public Integer getPageNo() {
        if (ObjectUtil.isNull(pageNo)) {
            return 1;
        }
        return pageNo;
    }

    public Integer getPageSize() {
        if (ObjectUtil.isNull(pageSize)) {
            return 10;
        }
        return pageSize;
    }
}
