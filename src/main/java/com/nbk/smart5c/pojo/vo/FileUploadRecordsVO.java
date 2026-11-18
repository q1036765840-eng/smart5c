package com.nbk.smart5c.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 文件上传记录视图对象
 * @since 2026-04-08 15:26:45
 */
@Data
@Accessors(chain = true)
@ApiModel("文件上传记录视图对象")
public class FileUploadRecordsVO {

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "视频时长")
    private Long videoDuration;

    @ApiModelProperty(value = "文件大小")
    private Long fileSize;

    @ApiModelProperty(value = "文件个数")
    private Integer fileCount;


    @ApiModelProperty(value = "文件夹名")
    private String folderName;

    @ApiModelProperty(value = "上传目录")
    private String uploadCatalog;

    @ApiModelProperty(value = "文件类型(0:视频,1:图片)")
    private Integer fileType;

    @ApiModelProperty(value = "文件网络访问地址")
    private String netPath;

    @ApiModelProperty(value = "展开地址")
    private String expandPath;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 创建时间
     */
    @ApiModelProperty("修改时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
