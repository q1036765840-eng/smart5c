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
 * @apiNote 文件夹创建记录表实体
 * @since 2026-06-30 18:45:01
 */
@Data
@Accessors(chain = true)
@TableName("nbk_folder_create")
@ApiModel("文件夹创建记录表实体")
public class FolderCreate extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "文件夹名")
    @TableField("folder_name")
    private String folderName;

    @ApiModelProperty(value = "文件夹路径")
    @TableField("folder_path")
    private String folderPath;
}
