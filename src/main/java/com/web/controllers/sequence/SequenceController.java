package com.web.controllers.sequence;

import com.web.base.common.CommonResponse;
import com.web.base.entity.PageResult;
import com.web.pojo.DTO.page.PageDTO;
import com.web.pojo.DTO.sequence.*;
import com.web.pojo.PO.user.UserPO;
import com.web.pojo.VO.request.RequestHistoryVO;
import com.web.pojo.VO.sequence.SequenceVO;
import com.web.services.sequence.SequenceService;
import com.web.services.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/sequence")
@Api(tags = "序列接口")
public class SequenceController {
	@Resource
	private UserService userService;

	@Resource
	private SequenceService sequenceService;

	@PostMapping("/createSequence")
	@ApiOperation(value = "创建用户序列", notes = "创建用户序列")
	public CommonResponse<String> createSequence(@RequestBody SequenceDTO sequenceDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		sequenceService.createSequence(sequenceDTO, userPO);
		return CommonResponse.create(null, "创建序列成功");
	}

	@GetMapping("/getSequences")
	@ApiOperation(value = "获取用户序列", notes = "获取用户序列")
	public CommonResponse<List<SequenceVO>> getSequences(HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		List<SequenceVO> sequenceVOS = sequenceService.getSequences(userPO);
		return CommonResponse.create(sequenceVOS, "获取序列成功");
	}

	@PostMapping("/deleteSequence")
	@ApiOperation(value = "删除用户序列", notes = "删除用户序列")
	public CommonResponse<String> deleteSequence(@RequestBody DeleteSequenceDTO deleteSequenceDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		sequenceService.deleteSequence(deleteSequenceDTO, userPO);
		return CommonResponse.create(null, "删除序列成功");
	}

	@PostMapping("/updateSequence")
	@ApiOperation(value = "更新用户序列", notes = "更新用户序列")
	public CommonResponse<String> updateSequence(@RequestBody UpdateSequenceDTO updateSequenceDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		sequenceService.updateSequence(updateSequenceDTO, userPO);
		return CommonResponse.create(null, "更新序列成功");
	}

	@PostMapping("/updateSequenceEnabled")
	@ApiOperation(value = "更新用户序列可用状态", notes = "更新用户序列可用状态")
	public CommonResponse<String> updateSequenceEnabled(@RequestBody SequenceEnabledDTO sequenceEnabledDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		sequenceService.switchSequenceEnabled(sequenceEnabledDTO, userPO);
		return CommonResponse.create(null, "更新序列可用状态成功");
	}

	@PostMapping("/getRequestHistory")
	@ApiOperation(value = "获取用户请求历史", notes = "获取用户请求历史")
	public CommonResponse<PageResult<List<RequestHistoryVO>>> getRequestHistory(@RequestBody PageDTO pageDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		PageResult<List<RequestHistoryVO>> requestHistoryVOPage = sequenceService.getRequestHistory(pageDTO, userPO);
		return CommonResponse.create(requestHistoryVOPage, "获取用户请求历史成功");
	}

	@PostMapping("/getSequence")
	@ApiOperation(value = "获取用户序列", notes = "获取用户序列")
	public CommonResponse<SequenceVO> getSequence(@RequestBody GetSequenceDTO getSequenceDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		SequenceVO sequenceVO = sequenceService.getSequence(getSequenceDTO, userPO);
		return CommonResponse.create(sequenceVO, "获取序列成功");
	}

	@PostMapping("/getSequenceHistoryBySequenceId")
	@ApiOperation(value = "根据序列号获取序列历史", notes = "根据序列号获取序列历史")
	public CommonResponse<PageResult<List<RequestHistoryVO>>> getSequenceHistoryBySequenceId(@RequestBody GetSequenceHistoryPageDTO getSequenceHistoryPageDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		PageResult<List<RequestHistoryVO>> sequenceHistoryVOPage = sequenceService.getRequestHistoryBySequenceID(getSequenceHistoryPageDTO, userPO);
		return CommonResponse.create(sequenceHistoryVOPage, "获取序列历史成功");
	}

	@PostMapping("/deleteSequenceHistoryBySequenceId")
	@ApiOperation(value = "根据序列号删除序列历史", notes = "根据序列号删除序列历史")
	public CommonResponse<String> deleteSequenceHistoryBySequenceId(@RequestBody DeleteSequenceHistoryDTO deleteSequenceHistoryDTO, HttpServletRequest request) {
		UserPO userPO = userService.getRequestUser(request);
		sequenceService.deleteSequenceHistoryBySequenceID(deleteSequenceHistoryDTO, userPO);
		return CommonResponse.create(null, "删除序列历史成功");
	}
}