package com.web.mapper.user;

import com.web.pojo.PO.user.UserPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(UserPO record);

	int insertSelective(UserPO record);

	UserPO selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(UserPO record);

	int updateByPrimaryKey(UserPO record);

	UserPO selectByUserName(String userName);

	List<UserPO> selectAll();

	List<UserPO> selectByUserTel(@Param("userTel") String userTel);

	Boolean existsById(Integer id);

	List<Integer> getId();
}
