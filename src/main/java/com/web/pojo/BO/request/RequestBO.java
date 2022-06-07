package com.web.pojo.BO.request;

import cn.hutool.http.Method;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
@EqualsAndHashCode
public class RequestBO {
	@ApiModelProperty(value = "全局id")
	private String globalId;

	@ApiModelProperty(value = "请求名")
	private String name;

	@ApiModelProperty(value = "请求描述")
	private String desc;

	@ApiModelProperty(value = "请求url")
	private String url;

	@ApiModelProperty(value = "请求方法")
	private Method method;

	@ApiModelProperty(value = "请求体")
	private String body;

	@ApiModelProperty(value = "请求头")
	private Map<String, String> headers;
}