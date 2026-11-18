package com.nbk.smart5c.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 误报请求参数对象
 * @since 2026-01-30 17:29:19
 */
@Data
@ToString
@Accessors(chain = true)
@ApiModel(value = "误报请求参数对象")
public class MisreportReqBO {
    @ApiModelProperty(value = "审核结果id")
    @NotNull(message = "misreport不能为空")
    private Long resultId;
    @ApiModelProperty(value = "是否误报0:否,1:是")
    @NotNull(message = "misreport不能为空")
    @Min(value = 0, message = "misreport取值只能为0或1")
    @Max(value = 1, message = "misreport取值只能为0或1")
    private Integer misreport;
}
