package com.web.pojo.VO.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class UserVO {
	@ApiModelProperty(value = "用户id")
	private Integer id;

	@ApiModelProperty(value = "用户名")
	private String userName;

	@ApiModelProperty(value = "密码")
	private String userPassword;

	@ApiModelProperty(value = "手机号")
	private String userTel;

	@ApiModelProperty(value = "邮箱")
	private String userEmail;

	@ApiModelProperty(value = "学号")
	private String userStuNo;

	@ApiModelProperty(value = "头像")
	private String userAvatar;

	@ApiModelProperty(value = "权限码")
	private Integer userRole;

	@ApiModelProperty(value = "权限名称")
	private String userRoleName;
}