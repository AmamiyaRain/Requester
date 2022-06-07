package com.web.services.sms.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.Log;
import com.alibaba.fastjson.JSON;
import com.web.base.entity.sms.SMSSendingEntity;
import com.web.base.entity.sms.SMSValidateEntity;
import com.web.base.enums.BusinessErrorEnum;
import com.web.base.exceptions.BusinessException;
import com.web.pojo.DTO.sms.SendMessageDTO;
import com.web.pojo.DTO.sms.ValidateVerificationCodeDTO;
import com.web.pojo.PO.user.UserPO;
import com.web.services.sms.SMSService;
import com.web.util.redis.RedisUtil;
import com.web.util.security.SyncUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SMSServiceImpl implements SMSService {
	private static final Log log = Log.get();

	@Override
	public void validateVerificationCode(ValidateVerificationCodeDTO validateVerificationCodeDTO, UserPO userPO) {
		if (StringUtils.isEmpty(validateVerificationCodeDTO.getPhone()) || StringUtils.isEmpty(validateVerificationCodeDTO.getVcode())) {
			throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
		}
		log.info("【ValidateVerificationCode】用户 {} 正在验证短信验证码：{}", userPO.getUserName(), validateVerificationCodeDTO);
		if (SyncUtil.start(validateVerificationCodeDTO)) {
			try {
				SMSValidateEntity smsValidateEntity = new SMSValidateEntity();
				BeanUtils.copyProperties(validateVerificationCodeDTO, smsValidateEntity);
				int result;
				try {
					result = Integer.parseInt(HttpUtil.post("https://sms.vaptcha.com/verify", JSON.toJSONString(smsValidateEntity)));
				} catch (IORuntimeException e) {
					throw new BusinessException(BusinessErrorEnum.CHECK_VERIFICATION_CODE_ERROR);
				}
				log.info("【ValidateVerificationCode】用户 {} 验证短信验证码结果：{}", userPO.getUserName(), result);
				if (result == 600) {
					if (validateVerificationCodeDTO.getPhone().equals(userPO.getUserTel())) {
						RedisUtil.put(RedisUtil.generateUserTempAuthKey(userPO.getId()), true, 10, TimeUnit.MINUTES);
					}
				} else {
					throw new BusinessException(BusinessErrorEnum.CHECK_VERIFICATION_CODE_FAILED);
				}
			} finally {
				SyncUtil.finish(validateVerificationCodeDTO);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	public void sendMessage(SMSSendingEntity smsSendingEntity, UserPO userPO) {
		if (SyncUtil.start(smsSendingEntity)) {
			try {
				log.info("【SendMessage】用户 {} 正在发送短信：{}", userPO.getUserName(), smsSendingEntity);
				String result;
				try {
					result = HttpUtil.post("https://sms.vaptcha.com/send", JSON.toJSONString(smsSendingEntity));
				} catch (IORuntimeException e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.SEND_VERIFICATION_CODE_ERROR);
				}
				log.info("【SendMessage】用户 {} 发送短信结果：{}", userPO.getUserName(), result);
				if (Integer.parseInt(result) != 200) {
					throw new BusinessException(BusinessErrorEnum.SEND_VERIFICATION_CODE_ERROR);
				}
			} finally {
				SyncUtil.finish(smsSendingEntity);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public void sendMessageForEveryone(SendMessageDTO sendMessageDTO, UserPO userPO) {
		if (SyncUtil.start(sendMessageDTO)) {
			try {
				if (RedisUtil.exists(RedisUtil.generateSendSMSCoolDownKey(sendMessageDTO.getRemoteAddr())) ||
					RedisUtil.exists(RedisUtil.generateSendSMSCoolDownKey(userPO.getUserName())) ||
					RedisUtil.exists(RedisUtil.generateSendSMSCoolDownKey(sendMessageDTO.getUserTel()))
				) {
					throw new BusinessException(BusinessErrorEnum.SEND_VERIFICATION_CODE_COOLING);
				}
				SMSSendingEntity smsSendingEntity = new SMSSendingEntity();
				smsSendingEntity.setToken(sendMessageDTO.getCaptchaRequest().getToken());
				smsSendingEntity.setPhone(sendMessageDTO.getUserTel());
				sendMessage(smsSendingEntity, userPO);
				RedisUtil.put(RedisUtil.generateSendSMSCoolDownKey(sendMessageDTO.getRemoteAddr()), true, 2, TimeUnit.MINUTES);
				RedisUtil.put(RedisUtil.generateSendSMSCoolDownKey(userPO.getUserName()), true, 2, TimeUnit.MINUTES);
				RedisUtil.put(RedisUtil.generateSendSMSCoolDownKey(sendMessageDTO.getUserTel()), true, 2, TimeUnit.MINUTES);
			} finally {
				SyncUtil.finish(sendMessageDTO);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public void sendMessageForSelf(SendMessageDTO sendMessageDTO, UserPO userPO) {
		if (SyncUtil.start(sendMessageDTO)) {
			try {
				if (RedisUtil.exists(RedisUtil.generateSendSMSCoolDownKey(sendMessageDTO.getRemoteAddr())) ||
					RedisUtil.exists(RedisUtil.generateSendSMSCoolDownKey(userPO.getUserName())) ||
					RedisUtil.exists(RedisUtil.generateSendSMSCoolDownKey(sendMessageDTO.getUserTel()))
				) {
					throw new BusinessException(BusinessErrorEnum.SEND_VERIFICATION_CODE_COOLING);
				}
				// 手机号对了才发短信
				if (sendMessageDTO.getUserTel().equals(userPO.getUserTel())) {
					SMSSendingEntity smsSendingEntity = new SMSSendingEntity();
					smsSendingEntity.setToken(sendMessageDTO.getCaptchaRequest().getToken());
					smsSendingEntity.setPhone(sendMessageDTO.getUserTel());
					sendMessage(smsSendingEntity, userPO);
				} else {
					RedisUtil.put(RedisUtil.generateSendSMSCoolDownKey(sendMessageDTO.getRemoteAddr()), true, 2, TimeUnit.MINUTES);
					RedisUtil.put(RedisUtil.generateSendSMSCoolDownKey(userPO.getUserName()), true, 2, TimeUnit.MINUTES);
					RedisUtil.put(RedisUtil.generateSendSMSCoolDownKey(sendMessageDTO.getUserTel()), true, 2, TimeUnit.MINUTES);
				}
			} finally {
				SyncUtil.finish(sendMessageDTO);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}
}