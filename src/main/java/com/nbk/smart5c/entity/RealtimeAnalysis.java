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
 * @apiNote 实时分析表实体：记录 mountpath 源数据目录的复制与解析状态
 * @since 2026-11-03
 */
@Data
@Accessors(chain = true)
@TableName("nbk_realtime_analysis")
@ApiModel("实时分析表实体")
public class RealtimeAnalysis extends BaseEntity {

    /** 状态：处理成功 */
    public static final String STATUS_SUCCESS = "Y";
    /** 状态：处理失败/未处理，等待下一轮重试 */
    public static final String STATUS_FAIL = "N";

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "源数据目录路径")
    @TableField("source_filepath")
    private String sourceFilepath;

    @ApiModelProperty(value = "复制后的目标目录路径")
    @TableField("dest_filepath")
    private String destFilepath;

    @ApiModelProperty(value = "复制状态(Y成功,N失败)")
    @TableField("copy_status")
    private String copyStatus;

    @ApiModelProperty(value = "解析状态(Y成功,N失败)")
    @TableField("parse_status")
    private String parseStatus;
}
