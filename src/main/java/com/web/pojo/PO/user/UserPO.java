package com.web.pojo.PO.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@ToString
@EqualsAndHashCode
public class UserPO implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 主键自增id
	 */
	@ApiModelProperty(value = "主键自增id")
	private Integer id;

	/**
	 * 用户名
	 */
	@ApiModelProperty(value = "用户名")
	private String userName;

	/**
	 * 手机号
	 */
	@ApiModelProperty(value = "手机号")
	private String userTel;

	/**
	 * 密码
	 */
	@ApiModelProperty(value = "密码")
	private String userPassword;

	/**
	 * 注册邮箱
	 */
	@ApiModelProperty(value = "注册邮箱")
	private String userEmail;

	/**
	 * 注册学号
	 */
	@ApiModelProperty(value = "注册学号")
	private String userStuNo;

	/**
	 * 用户头像
	 */
	@ApiModelProperty(value = "用户头像")
	private String userAvatar;

	/**
	 * 用户权限
	 */
	@ApiModelProperty(value = "角色")
	private Integer userRole;

	/**
	 * 密码盐
	 */
	@ApiModelProperty(value = "密码盐")
	private String userSalt;
}