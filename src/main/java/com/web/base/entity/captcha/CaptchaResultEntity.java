package com.web.base.entity.captcha;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Data
public class CaptchaResultEntity {
	private Integer success;

	private Integer score;

	private String msg;
}