package com.web.pojo.DTO.user;

import com.web.pojo.DTO.captcha.CaptchaDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@EqualsAndHashCode
public class UserRegisterDTO implements Serializable {
	/**
	 * 用户名
	 */
	@ApiModelProperty(value = "用户名", required = true)
	private String userName;

	/**
	 * 密码
	 */
	@ApiModelProperty(value = "密码", required = true)
	private String userPassword;

	@ApiModelProperty(value = "验证码部分", required = true)
	private CaptchaDTO captchaRequest;

	public UserRegisterDTO() {
	}
}