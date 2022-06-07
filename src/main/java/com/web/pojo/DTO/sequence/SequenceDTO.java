package com.web.pojo.DTO.sequence;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@EqualsAndHashCode
public class SequenceDTO {
	/**
	 * 序列名
	 */
	@ApiModelProperty(value = "序列名")
	private String sequenceName;

	/**
	 * 序列描述
	 */
	@ApiModelProperty(value = "序列描述")
	private String sequenceDesc;

	/**
	 * 序列是否需要继承cookie
	 */
	@ApiModelProperty(value = "序列是否需要继承cookie")
	private Boolean isCookieInherit;

	/**
	 * 序列所拥有的请求
	 */
	@ApiModelProperty(value = "序列所拥有的请求")
	private List<Integer> sequenceRequests;

	@ApiModelProperty(value = "序列循环时间")
	private Integer sequenceRepeatTime;

	@ApiModelProperty(value = "序列循环时间单位")
	private String sequenceRepeatTimeUnit;
}