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
 * @apiNote 文件上传记录表实体
 * @since 2026-05-15 11:02:04
 */
@Data
@Accessors(chain = true)
@TableName("nbk_file_upload_records")
@ApiModel("文件上传记录表实体")
public class FileUploadRecords extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "文件夹名")
    @TableField("folder_name")
    private String folderName;

    @ApiModelProperty(value = "上传目录")
    @TableField("upload_path")
    private String uploadPath;

}
