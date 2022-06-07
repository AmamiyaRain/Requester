package com.web.base.entity.sms;

import com.web.configs.sms.SMSConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class SMSValidateEntity {
	private String smsid;

	private String smskey;

	private String phone;

	private String vcode;

	public SMSValidateEntity() {
		this.smsid = SMSConfig.getSmsid();
		this.smskey = SMSConfig.getSmskey();
	}
}