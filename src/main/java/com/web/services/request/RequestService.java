package com.web.services.request;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.web.pojo.BO.request.RequestBO;
import com.web.pojo.DTO.request.EditRequestDTO;
import com.web.pojo.DTO.request.RequestDTO;
import com.web.pojo.DTO.request.RequestIndexDTO;
import com.web.pojo.PO.sequence.SequencePO;
import com.web.pojo.PO.user.UserPO;
import com.web.pojo.VO.request.RequestHistoryVO;
import com.web.pojo.VO.request.RequestResponseVO;
import com.web.pojo.VO.request.RequestVO;

import java.util.List;

public interface RequestService {
	//从requestBO创建httpRequest
	HttpRequest createRequest(RequestBO requestBO, Boolean isCookieInherit);

	HttpResponse sendRequest(HttpRequest request);

	RequestVO saveRequest(RequestDTO requestDTO, UserPO userPO);

	List<RequestVO> getUserRequests(UserPO userPO);

	void editRequest(EditRequestDTO editRequestDTO, UserPO userPO);

	void deleteRequest(RequestIndexDTO requestIndexDTO, UserPO userPO);

	RequestResponseVO sendRequestByIndex(RequestIndexDTO requestIndexDTO, UserPO userPO);

	List<RequestHistoryVO> sendRequestBySequences(SequencePO sequencePO);

	void testSend(UserPO userPO);
}