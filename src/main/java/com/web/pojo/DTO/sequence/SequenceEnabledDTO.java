package com.web.pojo.DTO.sequence;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class SequenceEnabledDTO {
	@ApiModelProperty(value = "自增ID")
	private Integer id;

	@ApiModelProperty(value = "序列可用性")
	private Boolean sequenceEnabled;
}