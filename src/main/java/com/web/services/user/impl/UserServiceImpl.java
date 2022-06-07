package com.web.services.user.impl;

import cn.hutool.log.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.base.constants.PermissionConstant;
import com.web.base.entity.PageResult;
import com.web.base.enums.BusinessErrorEnum;
import com.web.base.exceptions.BusinessException;
import com.web.mapper.role.RoleMapper;
import com.web.mapper.user.UserMapper;
import com.web.pojo.DTO.page.PageDTO;
import com.web.pojo.DTO.sms.ValidateVerificationCodeDTO;
import com.web.pojo.DTO.user.UserDeleteDTO;
import com.web.pojo.DTO.user.UserLoginDTO;
import com.web.pojo.DTO.user.UserModifyPasswordDTO;
import com.web.pojo.DTO.user.UserRegisterDTO;
import com.web.pojo.PO.role.RolePO;
import com.web.pojo.PO.user.UserPO;
import com.web.pojo.VO.user.UserLoginStateVO;
import com.web.pojo.VO.user.UserTokenVO;
import com.web.pojo.VO.user.UserVO;
import com.web.services.oss.OssService;
import com.web.services.permission.PermissionService;
import com.web.services.sms.SMSService;
import com.web.services.user.UserService;
import com.web.util.redis.RedisUtil;
import com.web.util.security.SecurityUtil;
import com.web.util.security.SyncUtil;
import com.web.util.security.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
	private final static Log log = Log.get();

	@Resource
	private OssService ossService;

	@Resource
	private UserMapper userMapper;

	@Resource
	private RoleMapper roleMapper;

	@Resource
	private PermissionService permissionService;

	@Resource
	private SMSService smsService;

	@Override
	public void register(UserRegisterDTO userRegisterDTO) {
		if (StringUtils.isEmpty(userRegisterDTO.getUserPassword()) || StringUtils.isEmpty(userRegisterDTO.getUserName())
		) {
			throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
		}
		log.info("【Register】{}", userRegisterDTO);
		if (SyncUtil.start(userRegisterDTO)) {
			try {
				if (userMapper.selectByUserName(userRegisterDTO.getUserName()) != null) {
					throw new BusinessException(BusinessErrorEnum.USER_ALREADY_EXISTS);
				}
				try {
					String salt = SecurityUtil.getDefaultLengthSalt();
					String encPassword = SecurityUtil.getMd5(userRegisterDTO.getUserPassword(), salt);
					UserPO userPO = new UserPO();
					userPO.setUserName(userRegisterDTO.getUserName());
					userPO.setUserPassword(encPassword);
					userPO.setUserSalt(salt);
					userPO.setUserRole(1);
					userMapper.insert(userPO);
				} catch (Exception e) {
					throw new BusinessException(BusinessErrorEnum.REGISTER_ERROR);
				}
			} finally {
				SyncUtil.finish(userRegisterDTO);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public UserLoginStateVO modifyAvatar(MultipartFile userAvatar, UserPO userPO) {
		if (userAvatar == null) {
			throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
		}
		log.info("【ModifyAvatar】用户 {} 正在修改头像：{}", userPO.getUserName(), userAvatar);
		if (SyncUtil.start("ModifyAvatar-%s".formatted(userPO))) {
			try {
				String userAvatarUrl = ossService.uploadImage(userAvatar);
				userPO.setUserAvatar(userAvatarUrl);
				BeanUtils.copyProperties(userPO, userPO);
				userMapper.updateByPrimaryKeySelective(userPO);
				log.info("【ModifyAvatar】用户 {} 修改头像成功：{}", userPO.getUserName(), userAvatarUrl);
				return getUserLoginState(userPO);
			} finally {
				SyncUtil.finish("ModifyAvatar-%s".formatted(userPO));
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public UserLoginStateVO login(UserLoginDTO userLoginDTO) {
		if (StringUtils.isEmpty(userLoginDTO.getUserName()) || StringUtils.isEmpty(userLoginDTO.getUserPassword())) {
			throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
		}
		log.info("【Login】{}", userLoginDTO);
		if (SyncUtil.start(userLoginDTO)) {
			try {
				UserPO userPO = userMapper.selectByUserName(userLoginDTO.getUserName());
				if (userPO == null) {
					throw new BusinessException(BusinessErrorEnum.USER_NOT_EXISTS);
				}
				String encPassword;
				try {
					encPassword = SecurityUtil.getMd5(userLoginDTO.getUserPassword(), userPO.getUserSalt());
				} catch (Exception e) {
					throw new BusinessException(BusinessErrorEnum.ABNORMAL_DATA);
				}
				if (!encPassword.equals(userPO.getUserPassword())) {
					throw new BusinessException(BusinessErrorEnum.LOGIN_FAILED);
				}
				log.info("【Login】用户 {} 登录成功。", userPO.getUserName(), userLoginDTO);
				return getUserLoginState(userPO);
			} finally {
				SyncUtil.finish(userLoginDTO);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public UserPO getRequestUser(HttpServletRequest request) {
		String token = request.getHeader(TokenUtil.getTokenHeader());
		if (token == null) {
			throw new BusinessException(BusinessErrorEnum.NOT_LOGGED_IN);
		}
		token = token.substring(7);
		if (TokenUtil.isExpired(token)) {
			throw new BusinessException(BusinessErrorEnum.TOKEN_EXPIRED);
		}
		UserTokenVO userTokenVO = TokenUtil.getTokenSubject(token, UserTokenVO.class);
		UserPO userPO = userMapper.selectByPrimaryKey(userTokenVO.getId());
		System.out.println("getRequestUser:");
		System.out.println(userPO);
		if (!userTokenVO.getUserName().equals(userPO.getUserName()) || !userTokenVO.getUserPassword().equals(userPO.getUserPassword())) {
			throw new BusinessException(BusinessErrorEnum.TOKEN_EXPIRED);
		}
		return userPO;
	}

	@Override
	public UserLoginStateVO getUserLoginState(UserPO userPO) {
		// 构造一份UserVO献给尊敬的userInfo先生
		UserVO userVO = new UserVO();
		BeanUtils.copyProperties(userPO, userVO);
		RolePO rolePO = roleMapper.selectByPrimaryKey(userPO.getUserRole());
		if (rolePO == null) {
			throw new BusinessException(BusinessErrorEnum.ROLE_NOT_EXISTS);
		}
		userVO.setUserRoleName(rolePO.getUserRoleName());

		// 构造一份UserTokenVO献给亲爱的userToken女士
		UserTokenVO userTokenVO = new UserTokenVO();
		BeanUtils.copyProperties(userPO, userTokenVO);

		// 构造一份Date献给可爱的expireTime小朋友
		Date now = new Date();

		try {
			UserLoginStateVO userLoginStateVO = new UserLoginStateVO();
			userLoginStateVO.setUserInfo(userVO);
			userLoginStateVO.setUserToken(TokenUtil.createJwt(userTokenVO, now));
			userLoginStateVO.setExpireTime(TokenUtil.getExpiredDate(now).getTime());
			return userLoginStateVO;
		} catch (JsonProcessingException e) {
			throw new BusinessException(BusinessErrorEnum.GENERATE_TOKEN_FAILED);
		}
	}

	@Override
	public void modifyPassword(UserModifyPasswordDTO userModifyPasswordDTO, UserPO userPO) {
		if (StringUtils.isEmpty(userModifyPasswordDTO.getOldPassword()) || StringUtils.isEmpty(userModifyPasswordDTO.getNewPassword())) {
			throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
		}
		log.info("【ModifyPassword】用户 {} 正在修改密码：{}", userPO.getUserName(), userModifyPasswordDTO);
		if (SyncUtil.start(userModifyPasswordDTO)) {
			try {
				if (StringUtils.isEmpty(userModifyPasswordDTO.getOldPassword()) || StringUtils.isEmpty(userModifyPasswordDTO.getNewPassword())) {
					throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
				}
				String oldPassword;
				System.out.println(userPO);
				try {
					oldPassword = SecurityUtil.getMd5(userModifyPasswordDTO.getOldPassword(), userPO.getUserSalt());
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.ABNORMAL_DATA);
				}
				if (!oldPassword.equals(userPO.getUserPassword())) {
					throw new BusinessException(BusinessErrorEnum.CHECK_OLD_PASSWORD_FAILED);
				}
				if (userModifyPasswordDTO.getOldPassword().equals(userModifyPasswordDTO.getNewPassword())) {
					throw new BusinessException(BusinessErrorEnum.PASSWORD_NOT_CHANGE);
				}
				try {
					userPO.setUserPassword(SecurityUtil.getMd5(userModifyPasswordDTO.getNewPassword(), userPO.getUserSalt()));
					userMapper.updateByPrimaryKey(userPO);
				} catch (Exception e) {
					throw new BusinessException(BusinessErrorEnum.MODIFY_PASSWORD_FAILED);
				}
				log.info("【ModifyPassword】用户 {} 修改密码成功。", userPO.getId(), userModifyPasswordDTO);
			} finally {
				SyncUtil.finish(userModifyPasswordDTO);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public void deleteUser(UserDeleteDTO userDeleteDTO, UserPO userPO) {
		if (SyncUtil.start(userDeleteDTO)) {
			if (userDeleteDTO.getUserId() == null) {
				throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
			}
			log.info("【DeleteUser】用户 {} 正在删除用户：{}", userPO.getUserName(), userDeleteDTO);
			try {
				if (!permissionService.checkUserPermissionExists(userPO, PermissionConstant.USER_MANAGEMENT)) {
					throw new BusinessException(BusinessErrorEnum.PERMISSION_DENIED);
				}
				if (userDeleteDTO.getUserId().equals(userPO.getId())) {
					throw new BusinessException(BusinessErrorEnum.CANNOT_DELETE_OWN_ACCOUNT);
				}
				if (!userMapper.existsById(userDeleteDTO.getUserId())) {
					throw new BusinessException(BusinessErrorEnum.USER_NOT_EXISTS);
				}
				userMapper.deleteByPrimaryKey(userDeleteDTO.getUserId());
				log.info("【DeleteUser】用户 {} 删除 {} 成功。", userPO.getUserName(), userDeleteDTO.getUserId());
			} finally {
				SyncUtil.finish(userDeleteDTO);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public PageResult<UserVO> getUserList(PageDTO pageDTO, UserPO userPO) {
		log.info("【GetUserList】用户 {} 正在获取用户列表：{}", userPO.getUserName(), pageDTO);
		if (SyncUtil.start(pageDTO)) {
			try {
				if (!permissionService.checkUserPermissionExists(userPO, PermissionConstant.USER_MANAGEMENT)) {
					throw new BusinessException(BusinessErrorEnum.PERMISSION_DENIED);
				}
				try {
					PageHelper.startPage(pageDTO.getPageIndex(), pageDTO.getPageSize());
				} catch (Exception e) {
					throw new BusinessException(BusinessErrorEnum.PAGE_PARAMETER_ERROR);
				}
				List<UserPO> userPOList = userMapper.selectAll();
				PageInfo<UserPO> pageInfo = new PageInfo<>(userPOList);
				List<UserVO> userVOList = new ArrayList<>();
				for (UserPO user : userPOList) {
					UserVO userVO = new UserVO();
					BeanUtils.copyProperties(user, userVO);
					userVO.setUserRoleName(roleMapper.selectByPrimaryKey(user.getUserRole()).getUserRoleName());
					userVOList.add(userVO);
				}
				log.info("【GetUserList】用户 {} 获取用户列表(长度为 {})成功。", userPO.getUserName(), userVOList.size());
				return new PageResult<>(pageInfo.getTotal(), userVOList);
			} finally {
				SyncUtil.finish(pageDTO);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public UserLoginStateVO bindPhone(ValidateVerificationCodeDTO validateVerificationCodeDTO, UserPO userPO) {
		if (StringUtils.isEmpty(validateVerificationCodeDTO.getPhone()) || StringUtils.isEmpty(validateVerificationCodeDTO.getVcode())) {
			throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
		}
		log.info("【BindPhone】用户 {} 正在绑定手机号：{}", userPO.getUserName(), validateVerificationCodeDTO);
		if (SyncUtil.start(validateVerificationCodeDTO + userPO.toString())) {
			try {
				if (!StringUtils.isEmpty(userPO.getUserTel()) && !RedisUtil.exists(RedisUtil.generateUserTempAuthKey(userPO.getId()))) {
					throw new BusinessException(BusinessErrorEnum.TEMP_AUTH_EXPIRED);
				}
				if (!permissionService.checkUserPermissionExists(userPO, PermissionConstant.NORMAL_USER)) {
					throw new BusinessException(BusinessErrorEnum.PERMISSION_DENIED);
				}
				if (validateVerificationCodeDTO.getPhone().equals(userPO.getUserTel())) {
					throw new BusinessException(BusinessErrorEnum.PHONE_NOT_CHANGE);
				}
				if (userMapper.selectByUserTel(validateVerificationCodeDTO.getPhone()).size() > 0) {
					throw new BusinessException(BusinessErrorEnum.PHONE_ALREADY_EXISTS);
				}
				smsService.validateVerificationCode(validateVerificationCodeDTO, userPO);
				userPO.setUserTel(validateVerificationCodeDTO.getPhone());
				userMapper.updateByPrimaryKey(userPO);
				log.info("【BindPhone】用户 {} 绑定手机号 {} 成功。", userPO.getUserName(), validateVerificationCodeDTO.getPhone());
				return getUserLoginState(userPO);
			} finally {
				SyncUtil.finish(validateVerificationCodeDTO + userPO.toString());
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public void checkUserTempAuth(UserPO userPO) {
		if (!StringUtils.isEmpty(userPO.getUserTel()) && !RedisUtil.exists(RedisUtil.generateUserTempAuthKey(userPO.getId()))) {
			throw new BusinessException(BusinessErrorEnum.TEMP_AUTH_EXPIRED);
		}
	}
}