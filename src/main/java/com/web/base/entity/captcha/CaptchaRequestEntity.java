package com.web.base.entity.captcha;

import com.web.configs.captcha.CaptchaConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class CaptchaRequestEntity {
	private String id;

	private String secretkey;

	private Integer scene;

	private String token;

	private String ip;

	public CaptchaRequestEntity() {
		this.id = CaptchaConfig.getId();
		this.secretkey = CaptchaConfig.getSecretkey();
		this.scene = CaptchaConfig.getScene();
	}
}