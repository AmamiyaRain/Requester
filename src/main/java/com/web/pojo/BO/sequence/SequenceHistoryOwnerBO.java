package com.web.pojo.BO.sequence;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@Data
@EqualsAndHashCode
public class SequenceHistoryOwnerBO {
	@ApiModelProperty(value = "序列历史所有者ID")
	private Integer sequenceOwnerId;

	@ApiModelProperty(value = "序列ID")
	private Integer sequenceId;

	@ApiModelProperty(value = "序列历史UUID")
	private String sequenceRedisUuid;
}