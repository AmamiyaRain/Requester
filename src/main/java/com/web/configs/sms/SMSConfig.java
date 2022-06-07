package com.web.configs.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
@ToString
@EqualsAndHashCode
public class SMSConfig {
	private static String smsid;

	private static String smskey;


	private static String countrycode;


	private static String templateid;

	public static String getSmsid() {
		return smsid;
	}

	@Value("${vaptcha-sms.smsid}")
	public void setSmsid(String smsid) {
		SMSConfig.smsid = smsid;
	}

	public static String getSmskey() {
		return smskey;
	}

	@Value("${vaptcha-sms.smskey}")
	public void setSmskey(String smskey) {
		SMSConfig.smskey = smskey;
	}

	public static String getCountrycode() {
		return countrycode;
	}

	@Value("${vaptcha-sms.contrycode}")
	public void setCountrycode(String countrycode) {
		SMSConfig.countrycode = countrycode;
	}

	public static String getTemplateid() {
		return templateid;
	}

	@Value("${vaptcha-sms.templateid}")
	public void setTemplateid(String templateid) {
		SMSConfig.templateid = templateid;
	}
}