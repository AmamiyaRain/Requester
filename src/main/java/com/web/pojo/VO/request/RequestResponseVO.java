package com.web.pojo.VO.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
@EqualsAndHashCode
public class RequestResponseVO {
	@ApiModelProperty(value = "响应头")
	private Map<String, List<String>> headers;

	@ApiModelProperty(value = "响应状态码")
	private int statusCode;

	@ApiModelProperty(value = "响应体")
	private String body;

	@ApiModelProperty(value = "响应时间")
	private long responseTime;
}