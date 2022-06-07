package com.web.pojo.DTO.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class UserModifyPasswordDTO {
	@ApiModelProperty(value = "原密码", required = true)
	private String oldPassword;

	@ApiModelProperty(value = "新密码", required = true)
	private String newPassword;
}