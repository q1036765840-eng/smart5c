package com.nbk.smart5c.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.nbk.smart5c.pojo.bo.DefectMarkBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 图片审核信息对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "图片审核信息对象")
public class ImageReviewInfoVO {

    @ApiModelProperty(value = "图片id")
    private Long imageId;

    @ApiModelProperty(value = "图片名称")
    private String imageName;

    @ApiModelProperty(value = "标记区域")
    private List<DefectMarkBO> markedRegions;

    @ApiModelProperty(value = "图片是否有效")
    @TableField(exist = false)
    private Integer imageValid;

    @ApiModelProperty(value = "缺陷描述")
    @TableField(exist = false)
    private String problemDesc;

    @ApiModelProperty(value = "分析人")
    @TableField(exist = false)
    private String analyzer;

    @ApiModelProperty(value = "受电弓状态")
    private String pantographStatus;

    @ApiModelProperty(value = "是否误报(0:否,1:是)")
    private Integer misreport;

    @ApiModelProperty(value = "确认状态(0:未查看,1:已查看,2:已确认)")
    private Integer confirmStatus;

    @ApiModelProperty(value = "置信度")
    private String reliability;
}
