package com.web.services.sms;

import com.web.pojo.DTO.sms.SendMessageDTO;
import com.web.pojo.DTO.sms.ValidateVerificationCodeDTO;
import com.web.pojo.PO.user.UserPO;

public interface SMSService {
	void validateVerificationCode(ValidateVerificationCodeDTO validateVerificationCodeDTO, UserPO userPO);

	void sendMessageForEveryone(SendMessageDTO sendMessageDTO, UserPO userPO);

	void sendMessageForSelf(SendMessageDTO sendMessageDTO, UserPO userPO);
}