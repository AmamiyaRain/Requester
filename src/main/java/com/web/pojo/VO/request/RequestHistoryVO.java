package com.web.pojo.VO.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class RequestHistoryVO {
	@ApiModelProperty(value = "序列历史某个请求的Request")
	RequestRequestVO request;

	@ApiModelProperty(value = "序列历史某个请求的Response")
	RequestResponseVO response;
}