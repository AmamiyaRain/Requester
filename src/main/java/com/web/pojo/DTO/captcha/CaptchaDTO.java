package com.web.pojo.DTO.captcha;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class CaptchaDTO {
	@ApiModelProperty(value = "验证码server地址", required = true)
	private String server;

	@ApiModelProperty(value = "验证码token", required = true)
	private String token;
}