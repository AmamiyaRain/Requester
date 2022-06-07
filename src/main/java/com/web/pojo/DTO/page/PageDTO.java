package com.web.pojo.DTO.page;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO {
	@ApiModelProperty(value = "当前页码")
	private int pageIndex;

	@ApiModelProperty(value = "每页显示条数")
	private int pageSize;
}