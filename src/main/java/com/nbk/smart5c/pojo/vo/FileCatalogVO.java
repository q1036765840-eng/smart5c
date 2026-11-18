package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName: PartIndexVo
 * @Author: Jiaxb
 * @CreateTime: 2022-03-08
 * @Description:
 */
@Data
@Accessors(chain = true)
@ApiModel("文件目录树视图对象")
public class FileCatalogVO {

    /**
     * 目录名
     */
    @ApiModelProperty(name = "catalogName", value = "目录名")
    private String catalogName;

    /**
     * 目录路径
     */
    @ApiModelProperty(name = "catalogPath", value = "目录路径")
    private String catalogPath;

    /**
     * 文件属性
     */
    @ApiModelProperty(name = "fileAttribute", value = "文件属性(0:文件夹,1:文件)")
    private Integer fileAttribute;

    /**
     * 有无权限
     */
    @ApiModelProperty(name = "enabled", value = "有无权限")
    private Boolean enabled;

    /**
     * 有子目录
     */
    @ApiModelProperty(name = "hasChildCatalog", value = "有子目录")
    private Boolean hasChildCatalog;

    /**
     * 子列表
     */
//    @ApiModelProperty(name = "childList", value = "子列表")
//    private List<FileCatalogVO> childList;

}
