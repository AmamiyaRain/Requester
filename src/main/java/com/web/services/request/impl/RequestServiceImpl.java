package com.web.services.request.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.*;
import cn.hutool.log.Log;
import com.alibaba.fastjson.JSON;
import com.web.base.enums.BusinessErrorEnum;
import com.web.base.exceptions.BusinessException;
import com.web.mapper.sequence.HistorySequenceMapper;
import com.web.mapper.sequence.SequenceMapper;
import com.web.pojo.BO.request.RequestBO;
import com.web.pojo.BO.sequence.SequenceRequestsListBO;
import com.web.pojo.DTO.request.EditRequestDTO;
import com.web.pojo.DTO.request.RequestDTO;
import com.web.pojo.DTO.request.RequestIndexDTO;
import com.web.pojo.PO.sequence.HistorySequencePO;
import com.web.pojo.PO.sequence.SequencePO;
import com.web.pojo.PO.user.UserPO;
import com.web.pojo.VO.request.RequestHistoryVO;
import com.web.pojo.VO.request.RequestRequestVO;
import com.web.pojo.VO.request.RequestResponseVO;
import com.web.pojo.VO.request.RequestVO;
import com.web.services.request.RequestService;
import com.web.util.redis.RedisUtil;
import com.web.util.security.SyncUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
	@Resource
	private SequenceMapper sequenceMapper;

	@Resource
	private HistorySequenceMapper historySequenceMapper;

	private final static Log log = Log.get();

	@Override
	public HttpRequest createRequest(RequestBO requestBO, Boolean isCookieInherit) {
		HttpRequest httpRequest;
		try {
			httpRequest = HttpUtil.createRequest(requestBO.getMethod(), requestBO.getUrl());
			if (isCookieInherit) {
				httpRequest.addHeaders(requestBO.getHeaders()).body(requestBO.getBody());
			} else {
				httpRequest.disableCookie();
				httpRequest.cookie(requestBO.getHeaders().get("Cookie"));
				httpRequest.addHeaders(requestBO.getHeaders()).body(requestBO.getBody());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(BusinessErrorEnum.GENERATE_HTTP_REQUEST_FAILED);
		}
		return httpRequest;
	}

	@Override
	public HttpResponse sendRequest(HttpRequest request) {
		try {
			//下面四行注释不要删 测试用
			//System.setProperty("http.proxyHost", "127.0.0.1");
			//System.setProperty("http.proxyPort", "8888");
			//System.setProperty("https.proxyHost", "127.0.0.1");
			//System.setProperty("https.proxyPort", "8888");
			return request.timeout(3000).execute();
		} catch (HttpException | IORuntimeException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(BusinessErrorEnum.SEND_HTTP_REQUEST_FAILED);
		}
	}

	@Override
	public RequestVO saveRequest(RequestDTO requestDTO, UserPO userPO) {
		if (SyncUtil.start(requestDTO)) {
			try {
				String requestKey = RedisUtil.generateUserRequestKey(userPO.getId());
				if (requestDTO.getUrl() == null || requestDTO.getName() == null || requestDTO.getMethod() == null) {
					throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
				}
				log.info("【SaveRequest】用户{}正在保存请求：{}", userPO.getUserName(), requestDTO);
				if (requestDTO.getBody().length() > 10000) {
					throw new BusinessException(BusinessErrorEnum.REQUEST_BODY_TOO_LONG);
				}
				RequestBO requestBO = new RequestBO();
				BeanUtils.copyProperties(requestDTO, requestBO);
				Method method = convertStringToMethod(requestDTO.getMethod().toUpperCase(Locale.ROOT));
				if (method == null) {
					throw new BusinessException(BusinessErrorEnum.METHOD_CANT_BE_SUPPORTED);
				} else if (method == Method.GET && !Objects.equals(requestBO.getBody(), "")) {
					throw new BusinessException(BusinessErrorEnum.GET_METHOD_CANNOT_HAVE_BODY);
				}
				requestBO.setMethod(method);
				requestBO.setGlobalId(String.valueOf(System.currentTimeMillis()));
				int index;
				try {
					index = RedisUtil.updateListItem(requestKey, JSON.toJSON(requestBO));
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.REDIS_SAVE_FAILED);
				}
				RequestVO requestVO = new RequestVO();
				BeanUtils.copyProperties(requestBO, requestVO);
				requestVO.setIndex(index);
				requestVO.setMethod(requestDTO.getMethod().toUpperCase(Locale.ROOT));
				log.info("【SaveRequest】用户 {} 保存请求成功!", userPO.getUserName());
				return requestVO;
			} finally {
				SyncUtil.finish(requestDTO);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public List<RequestVO> getUserRequests(UserPO userPO) {
		if (SyncUtil.start(userPO + "getUserRequests")) {
			try {
				log.info("【GetUserRequests】用户{}正在获取请求列表", userPO.getUserName());
				List<String> requestValues = RedisUtil.getList(RedisUtil.generateUserRequestKey(userPO.getId()));
				List<RequestVO> requestVOS = new ArrayList<>();
				for (int i = 0; i < requestValues.size(); i++) {
					if (Objects.equals(requestValues.get(i), "Deleted")) {
						continue;
					}
					RequestVO requestVO = new RequestVO();
					RequestBO requestBO = JSON.parseObject(requestValues.get(i), RequestBO.class);
					BeanUtils.copyProperties(requestBO, requestVO);
					requestVO.setMethod(convertMethodToString(requestBO.getMethod()));
					requestVO.setIndex(i);
					requestVOS.add(requestVO);
				}
				log.info("【GetUserRequests】用户{}获取请求列表成功", userPO.getUserName());
				return requestVOS;
			} finally {
				SyncUtil.finish(userPO + "getUserRequests");
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	public RequestBO getRequestBOByIndex(Integer index, UserPO userPO) {
		if (SyncUtil.start(userPO + "getRequestBOByIndex")) {
			try {
				if (index == null) {
					throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
				}
				log.info("【GetRequestBOByIndex】用户{}正在获取第{}个请求", userPO.getUserName(), index);
				String request;
				String requestKey = RedisUtil.generateUserRequestKey(userPO.getId());
				int requestIndexMax = RedisUtil.getList(requestKey).size();
				if (index >= requestIndexMax) {
					throw new BusinessException(BusinessErrorEnum.REQUEST_INDEX_OUT_OF_RANGE);
				}
				try {
					request = RedisUtil.getListItem(RedisUtil.generateUserRequestKey(userPO.getId()), Long.valueOf(index));
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.REDIS_GET_FAILED);
				}
				log.info("【GetRequestBOByIndex】用户{}获取第{}个请求成功", userPO.getUserName(), index);
				return JSON.parseObject(request, RequestBO.class);
			} finally {
				SyncUtil.finish(userPO + "getRequestBOByIndex");
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public void editRequest(EditRequestDTO editRequestDTO, UserPO userPO) {
		if (SyncUtil.start(editRequestDTO)) {
			try {
				if (editRequestDTO.getUrl() == null || editRequestDTO.getName() == null || editRequestDTO.getMethod() == null || editRequestDTO.getIndex() == null) {
					throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
				}
				log.info("【EditRequest】用户{}正在修改第{}个请求", userPO.getUserName(), editRequestDTO.getIndex());
				if (editRequestDTO.getBody().length() > 10000) {
					throw new BusinessException(BusinessErrorEnum.REQUEST_BODY_TOO_LONG);
				}
				String requestKey = RedisUtil.generateUserRequestKey(userPO.getId());
				int requestIndexMax = RedisUtil.getList(requestKey).size();
				if (editRequestDTO.getIndex() >= requestIndexMax) {
					throw new BusinessException(BusinessErrorEnum.REQUEST_INDEX_OUT_OF_RANGE);
				}
				RequestBO requestBO = getRequestBOByIndex(editRequestDTO.getIndex(), userPO);
				BeanUtils.copyProperties(editRequestDTO, requestBO);
				Method method = convertStringToMethod(editRequestDTO.getMethod().toUpperCase(Locale.ROOT));
				if (method == null) {
					throw new BusinessException(BusinessErrorEnum.METHOD_CANT_BE_SUPPORTED);
				} else if (method == Method.GET && !Objects.equals(requestBO.getBody(), "")) {
					throw new BusinessException(BusinessErrorEnum.GET_METHOD_CANNOT_HAVE_BODY);
				}
				requestBO.setMethod(method);
				try {
					RedisUtil.setListItem(requestKey, editRequestDTO.getIndex(), JSON.toJSON(requestBO));
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.REDIS_SAVE_FAILED);
				}
				log.info("【EditRequest】用户{}修改第{}个请求成功", userPO.getUserName(), editRequestDTO.getIndex());
			} finally {
				SyncUtil.finish(editRequestDTO);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public void deleteRequest(RequestIndexDTO requestIndexDTO, UserPO userPO) {
		if (SyncUtil.start(requestIndexDTO + "deleteRequest")) {
			try {
				if (requestIndexDTO.getIndex() == null) {
					throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
				}
				log.info("【DeleteRequest】用户{}正在删除第{}个请求", userPO.getUserName(), requestIndexDTO.getIndex());
				String requestKey = RedisUtil.generateUserRequestKey(userPO.getId());
				int requestIndexMax = RedisUtil.getList(requestKey).size();
				if (requestIndexDTO.getIndex() >= requestIndexMax) {
					throw new BusinessException(BusinessErrorEnum.REQUEST_INDEX_OUT_OF_RANGE);
				}
				List<SequenceRequestsListBO> sequenceRequestsListBOS;
				try {
					sequenceRequestsListBOS = sequenceMapper.selectSequenceRequestsBySequenceOwnerId(userPO.getId());
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.REDIS_GET_FAILED);
				}
				for (SequenceRequestsListBO sequenceRequestsListBO : sequenceRequestsListBOS) {
					if (sequenceRequestsListBO.getSequenceRequests().contains(requestIndexDTO.getIndex())) {
						throw new BusinessException(BusinessErrorEnum.DELETING_WORKING_REQUEST);
					}
				}
				try {
					RedisUtil.setListItem(requestKey, requestIndexDTO.getIndex(), "Deleted");
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.REDIS_DELETE_FAILED);
				}
				log.info("【DeleteRequest】用户{}删除第{}个请求成功", userPO.getUserName(), requestIndexDTO.getIndex());
			} finally {
				SyncUtil.finish(requestIndexDTO + "deleteRequest");
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public RequestResponseVO sendRequestByIndex(RequestIndexDTO requestIndexDTO, UserPO userPO) {
		if (SyncUtil.start(requestIndexDTO + "sendRequestByIndex")) {
			try {
				if (requestIndexDTO.getIndex() == null) {
					throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
				}
				log.info("【SendRequestByIndex】用户{}正在发送第{}个请求", userPO.getUserName(), requestIndexDTO.getIndex());
				RequestBO requestBO = getRequestBOByIndex(requestIndexDTO.getIndex(), userPO);
				HttpRequest httpRequest = createRequest(requestBO, false);
				long startTime = System.currentTimeMillis();
				HttpResponse httpResponse = sendRequest(httpRequest);
				long endTime = System.currentTimeMillis();
				long respTime = endTime - startTime;
				log.info("【SendRequestByIndex】用户{}发送第{}个请求成功，耗时{}毫秒", userPO.getUserName(), requestIndexDTO.getIndex(), respTime);
				return generateRequestResponseVO(httpResponse, respTime);
			}finally {
				SyncUtil.finish(requestIndexDTO + "sendRequestByIndex");
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public List<RequestHistoryVO> sendRequestBySequences(SequencePO sequencePO) {
		List<HttpRequest> httpRequests = new ArrayList<>();
		List<String> userRequests = RedisUtil.getList(RedisUtil.generateUserRequestKey(sequencePO.getSequenceOwnerId()));
		List<RequestHistoryVO> requestHistoryVOS = new ArrayList<>();
		sequencePO.getSequenceRequests().forEach(sequenceRequestIndex -> {
			RequestBO requestBO = JSON.parseObject(userRequests.get(sequenceRequestIndex), RequestBO.class);
			httpRequests.add(createRequest(requestBO, sequencePO.getIsCookieInherit()));
			RequestHistoryVO requestHistoryVO = new RequestHistoryVO();
			RequestRequestVO requestRequestVO = new RequestRequestVO();
			requestRequestVO.setUrl(requestBO.getUrl());
			requestRequestVO.setMethod(convertMethodToString(requestBO.getMethod()));
			requestRequestVO.setHeaders(requestBO.getHeaders());
			requestRequestVO.setBody(requestBO.getBody());
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			requestRequestVO.setSendTime(formatter.format(new Date()));
			requestHistoryVO.setRequest(requestRequestVO);
			requestHistoryVOS.add(requestHistoryVO);
		});
		List<String> Strings = RedisUtil.generateUserSequenceHistorySetUUIDKey(sequencePO.getSequenceOwnerId());
		for (int i = 0; i < httpRequests.size(); i++) {
			Long beginTime = System.currentTimeMillis();
			System.out.println("Request " + i + " start at " + beginTime);
			System.out.println(httpRequests.get(i));
			HttpResponse httpResponse = sendRequest(httpRequests.get(i));
			System.out.println("Request " + i + " end at " + System.currentTimeMillis());
			Long endTime = System.currentTimeMillis();
			long respTime = endTime - beginTime;
			RequestResponseVO requestResponseVO = new RequestResponseVO();
			if (httpResponse == null) {
				requestResponseVO.setBody("time out");
			} else {
				requestResponseVO = generateRequestResponseVO(httpResponse, respTime);
			}
			requestHistoryVOS.get(i).setResponse(requestResponseVO);
		}
		requestHistoryVOS.forEach(requestHistoryVO -> RedisUtil.addListItem(Strings.get(0), JSON.toJSONString(requestHistoryVO)));
		HistorySequencePO historySequencePO = new HistorySequencePO();
		historySequencePO.setSequenceId(sequencePO.getId());
		historySequencePO.setSequenceRedisUuid(Strings.get(1));
		try {
			historySequenceMapper.insertSelective(historySequencePO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(BusinessErrorEnum.DATABASE_INSERT_FAILED);
		}
		return requestHistoryVOS;
	}

	private RequestResponseVO generateRequestResponseVO(HttpResponse httpResponse, long time) {
		RequestResponseVO requestResponseVO = new RequestResponseVO();
		requestResponseVO.setBody(httpResponse.body());
		requestResponseVO.setStatusCode(httpResponse.getStatus());
		//过滤掉key为空的header
		requestResponseVO.setHeaders(httpResponse.headers().entrySet().stream().filter(entry -> entry.getKey() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
		requestResponseVO.setResponseTime(time);
		return requestResponseVO;
	}

	private Method convertStringToMethod(String method) {
		return switch (method) {
			case "GET" -> Method.GET;
			case "POST" -> Method.POST;
			case "PUT" -> Method.PUT;
			case "DELETE" -> Method.DELETE;
			case "PATCH" -> Method.PATCH;
			case "HEAD" -> Method.HEAD;
			case "OPTIONS" -> Method.OPTIONS;
			default -> null;
		};
	}

	private String convertMethodToString(Method method) {
		return switch (method) {
			case GET -> "GET";
			case POST -> "POST";
			case PUT -> "PUT";
			case DELETE -> "DELETE";
			case PATCH -> "PATCH";
			case HEAD -> "HEAD";
			case OPTIONS -> "OPTIONS";
			default -> null;
		};
	}

	public void testSend(UserPO userPO) {
		List<String> requestValues = RedisUtil.getList(RedisUtil.generateUserRequestKey(userPO.getId()));

		requestValues.forEach(requestKey -> {
			RequestBO requestBO = JSON.parseObject(requestKey, RequestBO.class);
			HttpRequest httpRequest = createRequest(requestBO, false);
			sendRequest(httpRequest);
		});
	}
}