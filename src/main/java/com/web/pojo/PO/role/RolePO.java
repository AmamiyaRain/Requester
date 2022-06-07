package com.web.pojo.PO.role;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 用户角色表
 */
@ApiModel(value = "用户角色表")
@Data
public class RolePO {
	@ApiModelProperty(value = "id")
	private Integer id;

	/**
	 * 用户角色名
	 */
	@ApiModelProperty(value = "用户角色名")
	private String userRoleName;

	/**
	 * 用户权限组编码
	 */
	@ApiModelProperty(value = "用户权限组编码")
	private Long userRolePermissions;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	private Date createTime;
}