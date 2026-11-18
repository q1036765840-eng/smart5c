package com.nbk.smart5c.pojo.qo;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 缺陷库表查询对象
 * @since 2026-05-19 16:16:20
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "缺陷库表查询对象")
public class DefectStorageQO {
    @ApiModelProperty(value = "检测任务")
    private String taskName;
    @ApiModelProperty(value = "缺陷类型")
    private String defectType;
    @ApiModelProperty(value = "缺陷等级")
    private Integer defectLevel;
    @ApiModelProperty(value = "车号")
    private String locomotiveNumber;
    @ApiModelProperty(value = "区站名")
    private String stationName;
    @ApiModelProperty(value = "线路名称")
    private String lineName;
    @ApiModelProperty(value = "公里标")
    private String kmSign;
    @ApiModelProperty(value = "杆号")
    private String poleNo;
    @ApiModelProperty(value = "复核人")
    private String reviewer;
    @ApiModelProperty(value = "审核状态(0:未审核,1:通过,2:不通过)")
    private List<Integer> revieweStatusList;
    @ApiModelProperty(value = "0:缺陷管理，1:缺陷库")
    private String busiType;
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    @ApiModelProperty(name = "pageNo", value = "页号", required = true)
    private Integer pageNo;
    @ApiModelProperty(name = "pageSize", value = "页大小", required = true)
    private Integer pageSize;

    public Integer getPageNo() {
        if (ObjectUtil.isNull(pageNo)) {
            return 1;
        }
        return pageNo;
    }

    public Integer getPageSize() {
        if (ObjectUtil.isNull(pageSize)) {
            return 10;
        }
        return pageSize;
    }
}
