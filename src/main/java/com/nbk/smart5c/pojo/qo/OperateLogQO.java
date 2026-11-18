package com.nbk.smart5c.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 操作日志表查询对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "操作日志表查询对象")
public class OperateLogQO {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "操作人")
    private String operateUser;
    @ApiModelProperty(value = "操作类型(0:删除,1:新增,2:修改)")
    private Integer operateType;
    @ApiModelProperty(value = "操作类名")
    private String className;
    @ApiModelProperty(value = "操作方法名")
    private String methodName;
    @ApiModelProperty(value = "操作方法参数")
    private String methodParams;
    @ApiModelProperty(value = "操作方法返回值")
    private String returnValue;
    @ApiModelProperty(value = "操作耗时")
    private Long costTime;
    @ApiModelProperty(value = "操作详细描述")
    private String operateDesc;
}
