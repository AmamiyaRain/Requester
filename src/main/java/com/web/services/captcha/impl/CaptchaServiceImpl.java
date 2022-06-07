package com.web.services.captcha.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.web.base.entity.captcha.CaptchaRequestEntity;
import com.web.base.entity.captcha.CaptchaResultEntity;
import com.web.base.enums.BusinessErrorEnum;
import com.web.base.exceptions.BusinessException;
import com.web.pojo.DTO.captcha.CaptchaDTO;
import com.web.services.captcha.CaptchaService;
import com.web.util.redis.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaServiceImpl implements CaptchaService {
	private static final Log log = LogFactory.get();

	@Override
	public CaptchaResultEntity secondaryVerificationOnline(String serverURL, CaptchaRequestEntity captchaRequestEntity) {
		log.info("【secondaryVerificationOnline】正在向 {} 请求二次验证：{}", serverURL, captchaRequestEntity);
		try {
			String data = HttpUtil.post(serverURL, JSON.toJSONString(captchaRequestEntity));
			log.info("【secondaryVerificationOnline】向 {} 请求二次验证结果：{}", serverURL, data);
			return JSONObject.parseObject(data, CaptchaResultEntity.class);
		} catch (IORuntimeException e) {
			throw new BusinessException(BusinessErrorEnum.VAPTCHA_CHECK_ERROR);
		}
	}

	@Override
	public long getTokenExpireTime(String token) {
		return Long.parseLong(token.substring(0, token.length() - 11)) + 180;
	}

	@Override
	public void checkCaptchaToken(CaptchaDTO captchaDTO, HttpServletRequest request) {
		if (StringUtils.isEmpty(captchaDTO.getServer()) || StringUtils.isEmpty(captchaDTO.getToken())) {
			throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
		}
		if (RedisUtil.exists(RedisUtil.generateVerifiedVaptchaTokenKey(captchaDTO.getToken()))) {
			return;
		}
		CaptchaRequestEntity captchaRequestEntity = new CaptchaRequestEntity();
		captchaRequestEntity.setIp(request.getRemoteAddr());
		BeanUtils.copyProperties(captchaDTO, captchaRequestEntity);
		CaptchaResultEntity captchaResultEntity = secondaryVerificationOnline(captchaDTO.getServer(), captchaRequestEntity);
		if (captchaResultEntity.getSuccess() != 1) {
			throw new BusinessException(BusinessErrorEnum.VAPTCHA_CHECK_FAILED);
		}
		if (captchaResultEntity.getScore() < 60) {
			throw new BusinessException(BusinessErrorEnum.VAPTCHA_LOW_SCORE);
		}
		RedisUtil.put(RedisUtil.generateVerifiedVaptchaTokenKey(captchaDTO.getToken()), true, getTokenExpireTime(captchaDTO.getToken()) - System.currentTimeMillis() / 1000, TimeUnit.SECONDS);
	}
}