package com.web.configs.captcha;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@ToString
@EqualsAndHashCode
@Component
public class CaptchaConfig {
	private static String id;

	private static String secretkey;

	private static Integer scene;

	public static String getId() {
		return id;
	}

	@Value("${vaptcha.id}")
	public void setId(String id) {
		CaptchaConfig.id = id;
	}

	public static String getSecretkey() {
		return secretkey;
	}

	@Value("${vaptcha.secretkey}")
	public void setSecretkey(String secretkey) {
		CaptchaConfig.secretkey = secretkey;
	}

	public static Integer getScene() {
		return scene;
	}

	@Value("${vaptcha.scene}")
	public void setScene(Integer scene) {
		CaptchaConfig.scene = scene;
	}
}