package com.web.pojo.VO.sequence;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode
public class SequenceVO {
	@ApiModelProperty(value = "自增ID")
	private Integer id;

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
	 * 序列拥有者id
	 */
	@ApiModelProperty(value = "序列拥有者id")
	private Integer sequenceOwnerId;

	/**
	 * 序列所拥有的请求
	 */
	@ApiModelProperty(value = "序列所拥有的请求")
	private List<Integer> sequenceRequests;

	@ApiModelProperty(value = "序列可用性")
	private Boolean sequenceEnabled;

	@ApiModelProperty(value = "序列循环时间")
	private Integer sequenceRepeatTime;

	@ApiModelProperty(value = "序列循环时间单位")
	private String sequenceRepeatTimeUnit;

	@ApiModelProperty(value = "创建时间")
	private Date createTime;
}