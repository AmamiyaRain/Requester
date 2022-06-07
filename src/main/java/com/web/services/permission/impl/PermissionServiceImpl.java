package com.web.services.permission.impl;

import com.web.base.enums.BusinessErrorEnum;
import com.web.base.exceptions.BusinessException;
import com.web.mapper.role.RoleMapper;
import com.web.pojo.PO.role.RolePO;
import com.web.pojo.PO.user.UserPO;
import com.web.pojo.VO.user.UserVO;
import com.web.services.permission.PermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;

@Service
public class PermissionServiceImpl implements PermissionService {
	@Resource
	private RoleMapper roleMapper;

	@Override
	public boolean checkUserPermissionExists(UserVO userVO, BigInteger permissionCode) {
		return checkUserPermissionExists(permissionCode, userVO.getUserRole());
	}

	@Override
	public boolean checkUserPermissionExists(UserPO userPO, BigInteger permissionCode) {
		return checkUserPermissionExists(permissionCode, userPO.getUserRole());
	}

	private boolean checkUserPermissionExists(BigInteger permissionCode, Integer userRole) {
		RolePO rolePO;
		try {
			rolePO = roleMapper.selectByPrimaryKey((userRole));
		} catch (Exception e) {
			throw new BusinessException(BusinessErrorEnum.ROLE_NOT_EXISTS);
		}
		if (rolePO == null) {
			throw new BusinessException(BusinessErrorEnum.ROLE_NOT_EXISTS);
		}
		BigInteger userPermissions = new BigInteger(String.valueOf(rolePO.getUserRolePermissions()), 2);
		return userPermissions.and(permissionCode).equals(permissionCode);
	}
}