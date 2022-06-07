package com.web.pojo.DTO.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
@EqualsAndHashCode

public class EditRequestDTO {
	@ApiModelProperty(value = "index", required = true)
	private Integer index;

	@ApiModelProperty(value = "请求名", required = true)
	private String name;

	@ApiModelProperty(value = "请求描述")
	private String desc;

	@ApiModelProperty(value = "请求url", required = true)
	private String url;

	@ApiModelProperty(value = "请求方法", required = true)
	private String method;

	@ApiModelProperty(value = "请求体")
	private String body;

	@ApiModelProperty(value = "请求头")
	private Map<String, String> headers;
}