package com.web.base.common;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author NXY666&YBW
 */
@Data

public final class CommonResponse<T> implements Serializable {
	/**
	 * 响应码
	 */
	@ApiModelProperty(value = "响应码", required = true)
	private Integer code;

	/**
	 * 响应消息
	 */
	@ApiModelProperty(value = "响应消息")
	private String message;

	/**
	 * 响应数据
	 */
	@ApiModelProperty(value = "响应数据")
	private T data;

	public static <T> CommonResponse<T> create(T data, String message) {
		return CommonResponse.create(200, data, message);
	}

	public static <T> CommonResponse<T> create(Integer code, T data, String message) {
		CommonResponse<T> commonResponse = new CommonResponse<>();
		commonResponse.setCode(code);
		commonResponse.setMessage(message);
		commonResponse.setData(data);
		return commonResponse;
	}
}