package com.nbk.smart5c.pojo.qo;

import cn.hutool.core.util.ObjectUtil;
import com.nbk.smart5c.common.enums.SdgStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据表查询对象
 * @since 2026-01-27 15:25:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "巡查数据表查询对象")
public class InspectionDataQO {
    @ApiModelProperty(value = "机务段id")
    private Long sectionId;
    @ApiModelProperty(value = "线路id")
    private Long lineId;
    @ApiModelProperty(value = "区站id")
    private Long stationId;

    @ApiModelProperty(value = "设备id")
    private Long treeId;

    @ApiModelProperty(value = "树类型")
    private Integer treeType;

    @ApiModelProperty(value = "机车号")
    private String locomotiveNumber;
    @ApiModelProperty(value = "受电工状态（可多选）(全部:0,正常:1,异物:2,弯曲:3,崩块:4,缺失:5)")
    private List<Integer> pantographStatus;
    @ApiModelProperty(value = "分析状态(待审批:1,已审批:2)")
    private Integer analysisStatus;
    @ApiModelProperty(value = "确认状态(0:未查看,1:已查看,2:已确认)")
    private Integer confirmStatus;
    @ApiModelProperty(value = "误报状态:0(否),1(是)")
    private Integer misreport;
    @ApiModelProperty(value = "检测开始时间")
    private String startTime;
    @ApiModelProperty(value = "检测结束时间")
    private String endTime;
    @ApiModelProperty(name = "是否实时(0:否，1:是)")
    private Integer isLive;
    @ApiModelProperty(name = "刷新标识(0:否，1:是)")
    private Integer refreshFlag;
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

    /**
     * 是否选择了"全部"，全部时不按受电工状态过滤
     */
    public boolean isPantographAll() {
        return pantographStatus != null && pantographStatus.contains(SdgStatusEnum.ALL.getCode());
    }

    /**
     * 是否包含"正常"，正常按主表 pantograph_status 过滤
     */
    public boolean isPantographNormal() {
        return pantographStatus != null && pantographStatus.contains(SdgStatusEnum.NORMAL.getCode());
    }

    /**
     * 选中的缺陷类型名称（异物/弯曲/崩块/缺失），按审核结果 marked_regions 匹配
     */
    public List<String> getPantographDefectTypes() {
        if (pantographStatus == null) {
            return Collections.emptyList();
        }
        return pantographStatus.stream()
                .filter(code -> code != null && !SdgStatusEnum.ALL.getCode().equals(code) && !SdgStatusEnum.NORMAL.getCode().equals(code))
                .map(SdgStatusEnum::getNameByCode)
                .filter(ObjectUtil::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
    }
}
