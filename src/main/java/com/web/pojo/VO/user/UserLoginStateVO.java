package com.web.pojo.VO.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class UserLoginStateVO {
	@ApiModelProperty(value = "用户Token")
	private String userToken;

	@ApiModelProperty(value = "用户信息")
	private UserVO userInfo;

	@ApiModelProperty(value = "过期时间")
	private Long expireTime;
}