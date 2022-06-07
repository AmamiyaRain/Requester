package com.web.pojo.DTO.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class RequestIndexDTO {
	@ApiModelProperty(value = "index", required = true)
	private Integer index;
}