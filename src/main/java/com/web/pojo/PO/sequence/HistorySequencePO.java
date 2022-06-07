package com.web.pojo.PO.sequence;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel(value = "final_history_sequence")
@Data
public class HistorySequencePO {
	/**
	 * 自增id
	 */
	@ApiModelProperty(value = "自增id")
	private Integer id;

	/**
	 * 序列id
	 */
	@ApiModelProperty(value = "序列id")
	private Integer sequenceId;

	/**
	 * 序列对应redis内的uuid
	 */
	@ApiModelProperty(value = "序列对应redis内的uuid")
	private String sequenceRedisUuid;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	private Date createTime;
}