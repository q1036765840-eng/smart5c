package com.nbk.smart5c.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 文件上传记录表查询对象
 * @since 2026-04-08 15:26:45
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "文件上传记录表查询对象")
public class FileUploadRecordsQO {
    @ApiModelProperty(value = "文件路径")
    private String folderName;
    @ApiModelProperty(value = "文件类型")
    private Integer fileType;
    @ApiModelProperty(value = "文件名")
    private String fileName;
}
