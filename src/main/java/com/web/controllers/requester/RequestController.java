package com.web.controllers.requester;

import com.web.base.common.CommonResponse;
import com.web.pojo.DTO.request.EditRequestDTO;
import com.web.pojo.DTO.request.RequestDTO;
import com.web.pojo.DTO.request.RequestIndexDTO;
import com.web.pojo.PO.user.UserPO;
import com.web.pojo.VO.request.RequestResponseVO;
import com.web.pojo.VO.request.RequestVO;
import com.web.services.request.RequestService;
import com.web.services.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/request")
@Api(tags = "请求接口")
public class RequestController {
	@Resource
	private UserService userService;

	@Resource
	private RequestService requestService;

	@PostMapping("/createRequest")
	@ApiOperation(value = "创建请求", notes = "创建请求")
	public CommonResponse<RequestVO> createRequest(@RequestBody RequestDTO requestDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		RequestVO requestVO = requestService.saveRequest(requestDTO, userPO);
		return CommonResponse.create(requestVO, "创建请求成功");
	}

	@GetMapping("/getRequest")
	@ApiOperation(value = "获取用户所有请求", notes = "获取用户所有请求")
	public CommonResponse<List<RequestVO>> getRequest(HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		List<RequestVO> requestVOS = requestService.getUserRequests(userPO);
		return CommonResponse.create(requestVOS, "获取请求成功");
	}

	@PostMapping("/editRequest")
	@ApiOperation(value = "编辑请求", notes = "编辑请求")
	public CommonResponse<Void> editRequest(@RequestBody EditRequestDTO editRequestDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		requestService.editRequest(editRequestDTO, userPO);
		return CommonResponse.create(null, "编辑请求成功");
	}

	@PostMapping("/deleteRequest")
	@ApiOperation(value = "删除请求", notes = "删除请求")
	public CommonResponse<Void> deleteRequest(@RequestBody RequestIndexDTO requestIndexDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		requestService.deleteRequest(requestIndexDTO, userPO);
		return CommonResponse.create(null, "删除请求成功");
	}

	@PostMapping("/sendRequestByIndex")
	@ApiOperation(value = "根据Index发送请求", notes = "根据Index发送请求")
	public CommonResponse<RequestResponseVO> sendRequestByIndex(@RequestBody RequestIndexDTO requestIndexDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		RequestResponseVO requestResponseVO = requestService.sendRequestByIndex(requestIndexDTO, userPO);
		return CommonResponse.create(requestResponseVO, "发送请求成功");
	}
}