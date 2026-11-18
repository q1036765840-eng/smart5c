package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName: DiskStorageVO
 * @Author: abin
 * @CreateTime: 2022-03-08
 * @Description: 磁盘空间视图对象
 */
@Data
@Accessors(chain = true)
@ApiModel("磁盘空间视图对象")
public class DiskStorageVO {

    /**
     * 总大小
     */
    @ApiModelProperty(name = "totalSize", value = "总空间")
    private Long totalSize;

    /**
     * 占用空间
     */
    @ApiModelProperty(name = "occupiedSize", value = "占用空间")
    private Long occupiedSize;

}
