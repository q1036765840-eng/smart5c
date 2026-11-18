package com.nbk.smart5c.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 树对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "树对象")
public class TreeVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String label;

    @ApiModelProperty(value = "层级类型(1:供电段,2:线路,3:区站,4:支柱)")
    private Integer type;

    @ApiModelProperty(value = "子树列表")
    private List<TreeVO> children;

}
