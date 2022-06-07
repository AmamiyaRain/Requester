package com.web.pojo.VO.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@Data
@EqualsAndHashCode
public class UserAvatarVO {
	@ApiModelProperty(value = "头像地址")
	private String avatarUrl;
}