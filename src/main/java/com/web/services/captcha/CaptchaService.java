package com.web.services.captcha;

import com.web.base.entity.captcha.CaptchaRequestEntity;
import com.web.base.entity.captcha.CaptchaResultEntity;
import com.web.pojo.DTO.captcha.CaptchaDTO;

import javax.servlet.http.HttpServletRequest;

public interface CaptchaService {
	CaptchaResultEntity secondaryVerificationOnline(String serverURL, CaptchaRequestEntity captchaRequestEntity);

	long getTokenExpireTime(String token);

	void checkCaptchaToken(CaptchaDTO CaptchaDTO, HttpServletRequest httpServletRequest);
}