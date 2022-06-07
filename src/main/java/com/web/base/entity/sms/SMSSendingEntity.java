package com.web.base.entity.sms;

import com.web.configs.sms.SMSConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class SMSSendingEntity {
	private String smsid;

	private String smskey;

	private String token;

	private String[] data = {"_vcode"};

	private String countrycode;

	private String phone;

	private String templateid;

	public SMSSendingEntity() {
		this.smsid = SMSConfig.getSmsid();
		this.smskey = SMSConfig.getSmskey();
		this.countrycode = SMSConfig.getCountrycode();
		this.templateid = SMSConfig.getTemplateid();
	}
}