package com.web.pojo.DTO.sms;

import com.web.pojo.DTO.captcha.CaptchaDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class SendMessageDTO {
	@ApiModelProperty(value = "用户手机号", required = true)
	private String userTel;

	@ApiModelProperty(value = "IP地址")
	private String remoteAddr;

	@ApiModelProperty(value = "验证码部分", required = true)
	private CaptchaDTO captchaRequest;
}