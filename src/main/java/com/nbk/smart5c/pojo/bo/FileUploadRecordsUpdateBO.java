package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 文件上传记录表修改业务对象
 * @since 2026-05-11 11:17:01
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "文件上传记录表修改业务对象")
public class FileUploadRecordsUpdateBO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "视频时长")
    private Long videoDuration;

    @ApiModelProperty(value = "文件大小")
    private Long fileSize;

    @ApiModelProperty(value = "状态(0:待上传,1:已上传)")
    private Integer status;

    @ApiModelProperty(value = "文件夹名")
    private String folderName;

    @ApiModelProperty(value = "上传目录")
    private String uploadCatalog;

    @ApiModelProperty(value = "文件类型(0:视频,1:图片)")
    private Integer fileType;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
