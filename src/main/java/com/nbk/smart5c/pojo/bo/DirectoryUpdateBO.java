package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "目录更新业务对象")
public class DirectoryUpdateBO {
    @ApiModelProperty(value = "目录路径")
    private String dirPath;

    @ApiModelProperty(value = "目录名")
    private String dirName;
}
