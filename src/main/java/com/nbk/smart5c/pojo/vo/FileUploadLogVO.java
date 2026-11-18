package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 文件上传记录表视图对象
 * @since 2026-05-15 11:02:04
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "文件上传日志视图对象")
public class FileUploadLogVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "文件夹名")
    private String folderName;

    @ApiModelProperty(value = "上传目录")
    private String uploadPath;

    @ApiModelProperty(value = "创建人")
    private String createdUser;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "修改人")
    private String updatedUser;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updatedTime;
}
