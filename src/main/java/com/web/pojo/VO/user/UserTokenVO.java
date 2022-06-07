package com.web.pojo.VO.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class UserTokenVO {
	@ApiModelProperty(value = "用户id")
	private Integer id;

	@ApiModelProperty(value = "用户名")
	private String userName;

	@ApiModelProperty(value = "密码")
	private String userPassword;
}