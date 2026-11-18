package com.nbk.smart5c.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 操作日志表实体
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@TableName("nbk_operate_log")
@ApiModel("操作日志表实体")
public class OperateLog {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "操作人")
    @TableField("operate_user")
    private String operateUser;

    @ApiModelProperty(value = "操作时间")
    @TableField("operate_time")
    private LocalDateTime operateTime;

    @ApiModelProperty(value = "操作类型(0:删除,1:新增,2:修改)")
    @TableField("operate_type")
    private Integer operateType;

    @ApiModelProperty(value = "操作类名")
    @TableField("class_name")
    private String className;

    @ApiModelProperty(value = "操作方法名")
    @TableField("method_name")
    private String methodName;

    @ApiModelProperty(value = "操作方法参数")
    @TableField("method_params")
    private String methodParams;

    @ApiModelProperty(value = "操作方法返回值")
    @TableField("return_value")
    private String returnValue;

    @ApiModelProperty(value = "操作耗时")
    @TableField("cost_time")
    private Long costTime;

    @ApiModelProperty(value = "操作详细描述")
    @TableField("operate_desc")
    private String operateDesc;

}
