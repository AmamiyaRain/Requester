package com.web.base.entity;


import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@EqualsAndHashCode
public class PageResult<T> {
	@ApiModelProperty(value = "总条数")
	private long total;

	@ApiModelProperty(value = "查询结果")
	private List<T> list;

	public PageResult(Long total, List<T> list) {
		this.total = total;
		this.list = list;
	}

	public PageResult(PageInfo<T> pageInfo) {
		this.total = pageInfo.getTotal();
		this.list = pageInfo.getList();
	}
}