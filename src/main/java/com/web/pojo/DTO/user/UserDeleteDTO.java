package com.web.pojo.DTO.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class UserDeleteDTO {
	@ApiModelProperty(value = "用户id", required = true)
	private Integer userId;
}