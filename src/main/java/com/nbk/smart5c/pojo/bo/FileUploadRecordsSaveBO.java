package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 文件上传记录表新增业务对象
 * @since 2026-05-11 11:17:01
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "文件上传记录表新增业务对象")
public class FileUploadRecordsSaveBO {


    @ApiModelProperty(value = "上传路径")
    private String uploadPath;


    @ApiModelProperty(value = "上传文件名")
    private String folderName;

}
