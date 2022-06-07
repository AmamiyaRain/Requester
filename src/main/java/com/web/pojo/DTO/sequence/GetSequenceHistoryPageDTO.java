package com.web.pojo.DTO.sequence;

import com.web.pojo.DTO.page.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class GetSequenceHistoryPageDTO {
	@ApiModelProperty(value = "序列ID")
	private Integer id;

	@ApiModelProperty(value = "页面信息")
	private PageDTO page;
}