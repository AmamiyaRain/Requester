package com.web.controllers.user;

import com.web.base.common.CommonResponse;
import com.web.base.entity.PageResult;
import com.web.mapper.sequence.SequenceMapper;
import com.web.pojo.DTO.captcha.CaptchaDTO;
import com.web.pojo.DTO.page.PageDTO;
import com.web.pojo.DTO.sms.SendMessageDTO;
import com.web.pojo.DTO.sms.ValidateVerificationCodeDTO;
import com.web.pojo.DTO.user.UserDeleteDTO;
import com.web.pojo.DTO.user.UserLoginDTO;
import com.web.pojo.DTO.user.UserModifyPasswordDTO;
import com.web.pojo.DTO.user.UserRegisterDTO;
import com.web.pojo.PO.user.UserPO;
import com.web.pojo.VO.user.UserAvatarVO;
import com.web.pojo.VO.user.UserLoginStateVO;
import com.web.pojo.VO.user.UserVO;
import com.web.services.captcha.CaptchaService;
import com.web.services.sms.SMSService;
import com.web.services.user.UserService;
import com.web.util.redis.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户接口
 *
 * @author ybw
 */

@RestController
@RequestMapping("/user")
@Api(tags = "用户接口")
public class UserController {
	@Resource
	private UserService userService;

	@Resource
	private CaptchaService captchaService;

	@Resource
	private SMSService smsService;

	@Resource
	private SequenceMapper sequenceMapper;

	@PostMapping("/test")
	@ApiOperation(value = "test", notes = "test")
	public CommonResponse<UserPO> test(HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
//		List<SequencePO> sequencePOS = sequenceMapper.selectAllBySequenceOwnerId(userPO.getId());
//		sequencePOS.forEach(sequencePO ->{
//			System.out.println(sequencePO.getSequenceRequests());
//		});
		String key = RedisUtil.generateUserRequestKey(userPO.getId());
		return CommonResponse.create(null, "test");
	}

	@PostMapping("/register")
	@ApiOperation(value = "注册用户", notes = "注册用户")
	public CommonResponse<UserAvatarVO> register(@RequestBody UserRegisterDTO userRegisterDTO, HttpServletRequest request) {
		captchaService.checkCaptchaToken(userRegisterDTO.getCaptchaRequest(), request);
		userService.register(userRegisterDTO);
		return CommonResponse.create(null, "注册成功");
	}

	@PostMapping("/modifyAvatar")
	@ApiOperation(value = "修改头像", notes = "修改头像")
	public CommonResponse<UserLoginStateVO> modifyAvatar(@RequestPart MultipartFile userAvatar, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		UserLoginStateVO userLoginStateVO = userService.modifyAvatar(userAvatar, userPO);
		return CommonResponse.create(userLoginStateVO, "修改成功");
	}

	@PostMapping("/login")
	@ApiOperation(value = "登录用户", notes = "登录用户")
	public CommonResponse<UserLoginStateVO> login(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
		captchaService.checkCaptchaToken(userLoginDTO.getCaptchaRequest(), request);
		UserLoginStateVO userLoginStateVO = userService.login(userLoginDTO);
		return CommonResponse.create(userLoginStateVO, "登录成功");
	}

	@PostMapping("/modifyPassword")
	@ApiOperation(value = "修改密码", notes = "修改密码")
	public CommonResponse<String> modifyPassword(@RequestBody UserModifyPasswordDTO userModifyPasswordDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		userService.modifyPassword(userModifyPasswordDTO, userPO);
		return CommonResponse.create(null, "修改成功");
	}

	@PostMapping("/deleteUser")
	@ApiOperation(value = "删除用户", notes = "删除用户")
	public CommonResponse<String> deleteUser(@RequestBody UserDeleteDTO userDeleteDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		userService.deleteUser(userDeleteDTO, userPO);
		return CommonResponse.create(null, "删除成功");
	}

	@PostMapping("/getAllUserInfo")
	@ApiOperation(value = "获取所有用户信息", notes = "获取所有用户信息")
	public CommonResponse<PageResult<UserVO>> getAllUserInfo(@RequestBody PageDTO pageDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		PageResult<UserVO> pageInfo = userService.getUserList(pageDTO, userPO);
		return CommonResponse.create(pageInfo, "获取成功");
	}

	@PostMapping("/sendMessage")
	@ApiOperation(value = "发送短信验证码", notes = "发送短信验证码")
	public CommonResponse<Void> sendMessage(@RequestBody SendMessageDTO sendMessageDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		captchaService.checkCaptchaToken(sendMessageDTO.getCaptchaRequest(), request);
		sendMessageDTO.setRemoteAddr(request.getRemoteAddr());
		smsService.sendMessageForEveryone(sendMessageDTO, userPO);
		return CommonResponse.create(null, "短信验证码已发送，请注意查收");
	}

	@PostMapping("/sendMessageForSelf")
	@ApiOperation(value = "向自己发送短信验证码", notes = "向自己发送短信验证码")
	public CommonResponse<Void> sendMessageForSelf(@RequestBody SendMessageDTO sendMessageDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		captchaService.checkCaptchaToken(sendMessageDTO.getCaptchaRequest(), request);
		sendMessageDTO.setRemoteAddr(request.getRemoteAddr());
		smsService.sendMessageForSelf(sendMessageDTO, userPO);
		return CommonResponse.create(null, "短信验证码已发送，请注意查收");
	}

	@PostMapping("/validateVerificationCode")
	@ApiOperation(value = "验证短信验证码", notes = "验证短信验证码")
	public CommonResponse<Void> validateVerificationCode(@RequestBody ValidateVerificationCodeDTO validateVerificationCodeDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		smsService.validateVerificationCode(validateVerificationCodeDTO, userPO);
		return CommonResponse.create(null, "验证成功");
	}

	@PostMapping("/bindPhone")
	@ApiOperation(value = "绑定手机号", notes = "绑定手机号")
	public CommonResponse<UserLoginStateVO> bindPhone(@RequestBody ValidateVerificationCodeDTO validateVerificationCodeDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		UserLoginStateVO userLoginStateVO = userService.bindPhone(validateVerificationCodeDTO, userPO);
		return CommonResponse.create(userLoginStateVO, "绑定成功");
	}

	@GetMapping("/checkUserTempAuth")
	@ApiOperation(value = "获取用户临时权限", notes = "获取用户临时权限")
	public CommonResponse<Void> checkUserTempAuth(HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		userService.checkUserTempAuth(userPO);
		return CommonResponse.create(null, "临时权限处于有效状态");
	}

	@PostMapping("/checkCaptchaToken")
	@ApiOperation(value = "校验手势验证码", notes = "校验手势验证码")
	public CommonResponse<Void> checkCaptchaToken(@RequestBody CaptchaDTO captchaDTO, HttpServletRequest request) {
		userService.getRequestUser(request);
		captchaService.checkCaptchaToken(captchaDTO, request);
		return CommonResponse.create(null, "身份确认成功");
	}
}