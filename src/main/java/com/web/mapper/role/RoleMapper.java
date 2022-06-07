package com.web.mapper.role;

import com.web.pojo.PO.role.RolePO;

public interface RoleMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(RolePO record);

	int insertSelective(RolePO record);

	RolePO selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(RolePO record);

	int updateByPrimaryKey(RolePO record);
}