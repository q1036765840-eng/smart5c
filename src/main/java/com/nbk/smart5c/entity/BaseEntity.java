package com.nbk.smart5c.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体
 */
@Data
@Accessors(chain = true)
public class BaseEntity implements Serializable {

    @ApiModelProperty(value = "创建人")
    @TableField(value = "created_user", fill = FieldFill.INSERT)
    private String createdUser;

    @ApiModelProperty(value = "修改人")
    @TableField(value = "updated_user", fill = FieldFill.UPDATE)
    private String updatedUser;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "updated_time", fill = FieldFill.UPDATE)
    private LocalDateTime updatedTime;
}
