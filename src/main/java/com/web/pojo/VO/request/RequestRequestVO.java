package com.web.pojo.VO.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
@EqualsAndHashCode
public class RequestRequestVO {
	@ApiModelProperty(value = "请求头")
	private Map<String, String> headers;

	@ApiModelProperty(value = "请求体")
	private String body;

	@ApiModelProperty(value = "请求地址")
	private String url;

	@ApiModelProperty(value = "请求方法")
	private String method;

	@ApiModelProperty(value = "发送请求的时间")
	private String sendTime;
}