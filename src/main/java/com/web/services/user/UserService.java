package com.web.services.user;

import com.web.base.entity.PageResult;
import com.web.pojo.DTO.page.PageDTO;
import com.web.pojo.DTO.sms.ValidateVerificationCodeDTO;
import com.web.pojo.DTO.user.UserDeleteDTO;
import com.web.pojo.DTO.user.UserLoginDTO;
import com.web.pojo.DTO.user.UserModifyPasswordDTO;
import com.web.pojo.DTO.user.UserRegisterDTO;
import com.web.pojo.PO.user.UserPO;
import com.web.pojo.VO.user.UserLoginStateVO;
import com.web.pojo.VO.user.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
	void register(UserRegisterDTO userRegisterDTO);

	UserLoginStateVO login(UserLoginDTO userLoginDTO);

	UserPO getRequestUser(HttpServletRequest request);

	UserLoginStateVO getUserLoginState(UserPO userPO);

	void modifyPassword(UserModifyPasswordDTO userModifyPasswordDTO, UserPO userPO);

	void deleteUser(UserDeleteDTO userDeleteDTO, UserPO userPO);

	UserLoginStateVO modifyAvatar(MultipartFile userAvatar, UserPO userPO);

	PageResult<UserVO> getUserList(PageDTO pageDTO, UserPO userPO);

	UserLoginStateVO bindPhone(ValidateVerificationCodeDTO validateMessage, UserPO userPO);

	void checkUserTempAuth(UserPO userPO);
}