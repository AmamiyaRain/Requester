package com.web.pojo.BO.sequence;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@EqualsAndHashCode
public class SequenceRequestsListBO {
	/**
	 * 序列所拥有的请求
	 */
	@ApiModelProperty(value = "序列所拥有的请求")
	private List<Integer> sequenceRequests;
}