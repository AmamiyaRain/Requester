package com.web.util.json;

import com.alibaba.fastjson.TypeReference;

import java.util.List;


public class IntegerListTypeHandler extends ListTypeHandler<Integer> {
	@Override
	protected TypeReference<List<Integer>> specificType() {
		return new TypeReference<>() {
		};
	}
}