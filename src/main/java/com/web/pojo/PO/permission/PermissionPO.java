package com.web.pojo.PO.permission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 用户权限表
 */
@ApiModel(value = "用户权限表")
@Data
public class PermissionPO {
	@ApiModelProperty(value = "自增ID")
	private Integer id;

	/**
	 * 用户权限码
	 */
	@ApiModelProperty(value = "用户权限码")
	private Long userPermissionCode;

	/**
	 * 用户权限组名
	 */
	@ApiModelProperty(value = "用户权限组名")
	private String userPermissionName;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	private Date createTime;
}