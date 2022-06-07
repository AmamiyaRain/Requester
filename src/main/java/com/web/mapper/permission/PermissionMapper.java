package com.web.mapper.permission;

import com.web.pojo.PO.permission.PermissionPO;

public interface PermissionMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(PermissionPO record);

	int insertSelective(PermissionPO record);

	PermissionPO selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(PermissionPO record);

	int updateByPrimaryKey(PermissionPO record);
}