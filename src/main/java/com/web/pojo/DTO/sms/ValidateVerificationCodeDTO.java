package com.web.pojo.DTO.sms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@Data
@EqualsAndHashCode
public class ValidateVerificationCodeDTO {
	@ApiModelProperty(value = "phone", required = true)
	private String phone;

	@ApiModelProperty(value = "vcode", required = true)
	private String vcode;
}