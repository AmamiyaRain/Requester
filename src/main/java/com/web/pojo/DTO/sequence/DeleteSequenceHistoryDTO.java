package com.web.pojo.DTO.sequence;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class DeleteSequenceHistoryDTO {
	@ApiModelProperty(value = "序列ID")
	private Integer id;
}