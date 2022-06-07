package com.web.services.sequence.impl;

import cn.hutool.log.Log;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.base.constants.PermissionConstant;
import com.web.base.entity.PageResult;
import com.web.base.enums.BusinessErrorEnum;
import com.web.base.exceptions.BusinessException;
import com.web.mapper.sequence.HistorySequenceMapper;
import com.web.mapper.sequence.SequenceMapper;
import com.web.pojo.BO.sequence.SequenceHistoryOwnerBO;
import com.web.pojo.DTO.page.PageDTO;
import com.web.pojo.DTO.sequence.*;
import com.web.pojo.PO.sequence.SequencePO;
import com.web.pojo.PO.user.UserPO;
import com.web.pojo.VO.request.RequestHistoryVO;
import com.web.pojo.VO.sequence.SequenceVO;
import com.web.services.permission.PermissionService;
import com.web.services.sequence.SequenceService;
import com.web.util.redis.RedisUtil;
import com.web.util.security.SyncUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SequenceServiceImpl implements SequenceService {
	@Resource
	SequenceMapper sequenceMapper;

	@Resource
	HistorySequenceMapper historySequenceMapper;

	@Resource
	PermissionService permissionService;

	private final static Log log = Log.get();

	@Override
	public void createSequence(SequenceDTO sequenceDTO, UserPO userPO) {
		if (SyncUtil.start(sequenceDTO)) {
			try {
				if (sequenceDTO.getIsCookieInherit() == null || Strings.isEmpty(sequenceDTO.getSequenceName()) || sequenceDTO.getSequenceRepeatTime() == null || Strings.isEmpty(sequenceDTO.getSequenceRepeatTimeUnit())) {
					throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
				}
				if(Strings.isEmpty(userPO.getUserTel())){
					throw new BusinessException(BusinessErrorEnum.MISSING_USER_PHONE);
				}
				log.info("【CreateSequence】用户{}正在创建序列：{}", userPO.getUserName(), sequenceDTO);
				sequenceDTO.setSequenceRepeatTimeUnit(sequenceDTO.getSequenceRepeatTimeUnit().toUpperCase(Locale.ROOT));
				if (RedisUtil.convertStringToTimeUnit(sequenceDTO.getSequenceRepeatTimeUnit().toUpperCase(Locale.ROOT)) == null) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_REPEAT_TIME_UNIT_ERROR);
				}
				int sequenceCount;
				try {
					sequenceCount = sequenceMapper.countBySequenceOwnerId(userPO.getId());
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.DATABASE_QUERY_FAILED);
				}
				if (sequenceCount >= 10) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_COUNT_EXCEEDS_LIMIT);
				}
				String userRequestKey = RedisUtil.generateUserRequestKey(userPO.getId());
				int requestIndexMax = RedisUtil.getList(userRequestKey).size();
				sequenceDTO.getSequenceRequests().forEach(sequenceRequestIndex -> {
					if (sequenceRequestIndex >= requestIndexMax || sequenceRequestIndex < 0) {
						throw new BusinessException(BusinessErrorEnum.SEQUENCE_REQUEST_NOT_EXIST);
					}
				});
				if (sequenceDTO.getSequenceRepeatTime() <= 0) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_REPEAT_TIME_NOT_VALID);
				}
				if (RedisUtil.convertTimeStringToSeconds(sequenceDTO.getSequenceRepeatTimeUnit(), sequenceDTO.getSequenceRepeatTime()) < 60 * 60 && !permissionService.checkUserPermissionExists(userPO, PermissionConstant.UNLIMITED_REQUEST_REPEAT)) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_REPEAT_TIME_TOO_FAST);
				}
				if (RedisUtil.convertTimeStringToSeconds(sequenceDTO.getSequenceRepeatTimeUnit(), sequenceDTO.getSequenceRepeatTime()) > 24 * 60 * 60 && !permissionService.checkUserPermissionExists(userPO, PermissionConstant.UNLIMITED_REQUEST_REPEAT)) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_REPEAT_TIME_TOO_LONG);
				}
				SequencePO sequencePO = new SequencePO();
				BeanUtils.copyProperties(sequenceDTO, sequencePO);
				sequencePO.setSequenceOwnerId(userPO.getId());
				try {
					sequenceMapper.insertSelective(sequencePO);
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.DATABASE_INSERT_FAILED);
				}
				log.info("【CreateSequence】用户{}创建序列成功！", userPO.getUserName());
			} finally {
				SyncUtil.finish(sequenceDTO);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public void updateSequence(UpdateSequenceDTO updateSequenceDTO, UserPO userPO) {
		if (SyncUtil.start(updateSequenceDTO)) {
			try {
				if (updateSequenceDTO.getId() == null || updateSequenceDTO.getIsCookieInherit() == null || Strings.isEmpty(updateSequenceDTO.getSequenceName()) || updateSequenceDTO.getSequenceRepeatTime() == null || Strings.isEmpty(updateSequenceDTO.getSequenceRepeatTimeUnit())) {
					throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
				}
				log.info("【UpdateSequence】用户{}正在更新序列：{}", userPO.getUserName(), updateSequenceDTO);
				updateSequenceDTO.setSequenceRepeatTimeUnit(updateSequenceDTO.getSequenceRepeatTimeUnit().toUpperCase(Locale.ROOT));
				if (RedisUtil.convertStringToTimeUnit(updateSequenceDTO.getSequenceRepeatTimeUnit().toUpperCase(Locale.ROOT)) == null) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_REPEAT_TIME_UNIT_ERROR);
				}
				SequencePO sequencePO;
				try {
					sequencePO = sequenceMapper.selectByPrimaryKey(updateSequenceDTO.getId());
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.DATABASE_QUERY_FAILED);
				}
				if (sequencePO == null || !Objects.equals(sequencePO.getSequenceOwnerId(), userPO.getId())) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_NOT_EXIST);
				}
				if (RedisUtil.convertTimeStringToSeconds(updateSequenceDTO.getSequenceRepeatTimeUnit(), updateSequenceDTO.getSequenceRepeatTime()) < 60 * 60 && !permissionService.checkUserPermissionExists(userPO, PermissionConstant.UNLIMITED_REQUEST_REPEAT)) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_REPEAT_TIME_TOO_FAST);
				}
				if (RedisUtil.convertTimeStringToSeconds(updateSequenceDTO.getSequenceRepeatTimeUnit(), updateSequenceDTO.getSequenceRepeatTime()) > 24 * 60 * 60 && !permissionService.checkUserPermissionExists(userPO, PermissionConstant.UNLIMITED_REQUEST_REPEAT)) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_REPEAT_TIME_TOO_LONG);
				}
				if (updateSequenceDTO.getSequenceRequests().size() == 0) {
					sequencePO.setSequenceEnabled(false);
				} else {
					String userRequestKey = RedisUtil.generateUserRequestKey(userPO.getId());
					int requestIndexMax = RedisUtil.getList(userRequestKey).size();
					updateSequenceDTO.getSequenceRequests().forEach(sequenceRequestIndex -> {
						if (sequenceRequestIndex >= requestIndexMax || sequenceRequestIndex < 0) {
							throw new BusinessException(BusinessErrorEnum.SEQUENCE_REQUEST_NOT_EXIST);
						}
					});
				}
				BeanUtils.copyProperties(updateSequenceDTO, sequencePO);
				try {
					sequenceMapper.updateByPrimaryKeySelective(sequencePO);
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.DATABASE_UPDATE_FAILED);
				}
				log.info("【UpdateSequence】用户{}更新序列成功！", userPO.getUserName());
			} finally {
				SyncUtil.finish(updateSequenceDTO);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public void deleteSequence(DeleteSequenceDTO deleteSequenceDTO, UserPO userPO) {
		if (SyncUtil.start(deleteSequenceDTO+"deleteSequence")) {
			try {
				if (deleteSequenceDTO.getId() == null) {
					throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
				}
				log.info("【DeleteSequence】用户{}正在删除序列：{}", userPO.getUserName(), deleteSequenceDTO);
				SequencePO sequencePO;
				System.out.println(deleteSequenceDTO);
				try {
					sequencePO = sequenceMapper.selectByPrimaryKey(deleteSequenceDTO.getId());
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.DATABASE_QUERY_FAILED);
				}
				System.out.println(sequencePO + " " + sequencePO.getId());
				if (sequencePO.getId() == null) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_NOT_EXIST);
				}
				DeleteSequenceHistoryDTO deleteSequenceHistoryDTO = new DeleteSequenceHistoryDTO();
				deleteSequenceHistoryDTO.setId(deleteSequenceDTO.getId());
				deleteSequenceHistoryBySequenceID(deleteSequenceHistoryDTO, userPO);
				try {
					sequenceMapper.deleteByPrimaryKey(deleteSequenceDTO.getId());
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.DATABASE_DELETE_FAILED);
				}
				log.info("【DeleteSequence】用户{}删除序列成功！", userPO.getUserName());
			} finally {
				SyncUtil.finish(deleteSequenceDTO+"deleteSequence");
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public List<SequenceVO> getSequences(UserPO userPO) {
		if (SyncUtil.start(userPO + "getSequences")) {
			try {
				log.info("【GetSequences】用户{}正在获取序列列表！", userPO.getUserName());
				List<SequencePO> sequencePOList;
				try {
					sequencePOList = sequenceMapper.selectAllBySequenceOwnerId(userPO.getId());
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.DATABASE_QUERY_FAILED);
				}
				List<SequenceVO> sequenceVOList = new ArrayList<>();
				sequencePOList.forEach(sequencePO -> {
					SequenceVO sequenceVO = new SequenceVO();
					BeanUtils.copyProperties(sequencePO, sequenceVO);
					sequenceVOList.add(sequenceVO);
				});
				log.info("【GetSequences】用户{}获取序列列表成功！", userPO.getUserName());
				return sequenceVOList;
			} finally {
				SyncUtil.finish(userPO + "getSequences");
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public void switchSequenceEnabled(SequenceEnabledDTO sequenceEnabledDTO, UserPO userPO) {
		if (SyncUtil.start(sequenceEnabledDTO)) {
			try {
				if (sequenceEnabledDTO.getId() == null || sequenceEnabledDTO.getSequenceEnabled() == null) {
					throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
				}
				log.info("【SwitchSequenceEnabled】用户{}正在切换序列{}的启用状态！", userPO.getUserName(), sequenceEnabledDTO.getId());
				int result;
				SequencePO sequencePO = sequenceMapper.selectByPrimaryKey(sequenceEnabledDTO.getId());
				if (!Objects.equals(sequencePO.getSequenceOwnerId(), userPO.getId())) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_NOT_EXIST);
				}
				if (sequencePO.getSequenceRequests().size() == 0) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_DOES_NOT_HAVE_REQUEST);
				}
				try {
					result = sequenceMapper.updateSequenceEnabledById(sequenceEnabledDTO.getSequenceEnabled(), sequenceEnabledDTO.getId());
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.DATABASE_UPDATE_FAILED);
				}
				if (result == 0) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_NOT_EXIST);
				}
				log.info("【SwitchSequenceEnabled】用户{}切换序列{}的启用状态成功！", userPO.getUserName(), sequenceEnabledDTO.getId());
			} finally {
				SyncUtil.finish(sequenceEnabledDTO);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public SequenceVO getSequence(GetSequenceDTO getSequenceDTO, UserPO userPO) {
		if (SyncUtil.start(getSequenceDTO + "getSequence")) {
			try {
				if (getSequenceDTO.getId() == null) {
					throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
				}
				log.info("【GetSequence】用户{}正在获取序列{}！", userPO.getUserName(), getSequenceDTO.getId());
				SequencePO sequencePO;
				try {
					sequencePO = sequenceMapper.selectByPrimaryKey(getSequenceDTO.getId());
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.DATABASE_QUERY_FAILED);
				}
				if (sequencePO == null) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_NOT_EXIST);
				}
				SequenceVO sequenceVO = new SequenceVO();
				BeanUtils.copyProperties(sequencePO, sequenceVO);
				log.info("【GetSequence】用户{}获取序列{}成功！", userPO.getUserName(), getSequenceDTO.getId());
				return sequenceVO;
			} finally {
				SyncUtil.finish(getSequenceDTO + "getSequence");
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public PageResult<List<RequestHistoryVO>> getRequestHistory(PageDTO pageDTO, UserPO userPO) {
		if (SyncUtil.start(pageDTO + "getRequestHistory")) {
			try {
				if (pageDTO.getPageIndex() == 0 || pageDTO.getPageSize() == 0) {
					throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
				}
				log.info("【GetRequestHistory】用户{}正在获取请求历史！", userPO.getUserName());
				List<String> sequenceRedisUUIDList;
				List<List<RequestHistoryVO>> requestHistoryVOListList = new ArrayList<>();
				try {
					PageHelper.startPage(pageDTO.getPageIndex(), pageDTO.getPageSize());
				} catch (Exception e) {
					throw new BusinessException(BusinessErrorEnum.PAGE_PARAMETER_ERROR);
				}
				try {
					sequenceRedisUUIDList = sequenceMapper.selectSequenceHistoryBySequenceOwnerId(userPO.getId());
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.DATABASE_QUERY_FAILED);
				}
				PageInfo<String> pageInfo = new PageInfo<>(sequenceRedisUUIDList);
				sequenceRedisUUIDList.forEach(sequenceRedisUUID -> {
					List<RequestHistoryVO> requestHistoryVOList = new ArrayList<>();
					String redisKey = RedisUtil.generateUserSequenceHistoryGetUUIDKey(userPO.getId(), sequenceRedisUUID);
					List<String> historyList = RedisUtil.getList(redisKey);
					if (historyList.size() > 0) {
						historyList.forEach(history -> {
							RequestHistoryVO requestHistoryVO;
							requestHistoryVO = JSON.parseObject(history, RequestHistoryVO.class);
							requestHistoryVOList.add(requestHistoryVO);
						});
					}
					requestHistoryVOListList.add(requestHistoryVOList);
				});
				log.info("【GetRequestHistory】用户{}获取请求历史成功！", userPO.getUserName());
				return new PageResult<>(pageInfo.getTotal(), requestHistoryVOListList);
			} finally {
				SyncUtil.finish(pageDTO + "getRequestHistory");
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public PageResult<List<RequestHistoryVO>> getRequestHistoryBySequenceID(GetSequenceHistoryPageDTO getSequenceHistoryPageDTO, UserPO userPO) {
		if (SyncUtil.start(getSequenceHistoryPageDTO)) {
			try {
				if (getSequenceHistoryPageDTO.getId() == null || getSequenceHistoryPageDTO.getPage().getPageIndex() == 0 || getSequenceHistoryPageDTO.getPage().getPageSize() == 0) {
					throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
				}
				log.info("【GetRequestHistoryBySequenceID】用户{}正在获取序列{}的请求历史！", userPO.getUserName(), getSequenceHistoryPageDTO.getId());
				List<String> sequenceRedisUUIDList;
				List<List<RequestHistoryVO>> requestHistoryVOListList = new ArrayList<>();
				SequencePO sequencePO;
				try {
					sequencePO = sequenceMapper.selectByPrimaryKey(getSequenceHistoryPageDTO.getId());
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.DATABASE_QUERY_FAILED);
				}
				if (sequencePO == null || !Objects.equals(sequencePO.getSequenceOwnerId(), userPO.getId())) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_NOT_EXIST);
				}
				try {
					PageHelper.startPage(getSequenceHistoryPageDTO.getPage().getPageIndex(), getSequenceHistoryPageDTO.getPage().getPageSize());
				} catch (Exception e) {
					throw new BusinessException(BusinessErrorEnum.PAGE_PARAMETER_ERROR);
				}
				try {
					sequenceRedisUUIDList = historySequenceMapper.selectSequenceRedisUuidBySequenceId(getSequenceHistoryPageDTO.getId());
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.DATABASE_QUERY_FAILED);
				}
				PageInfo<String> pageInfo = new PageInfo<>(sequenceRedisUUIDList);
				sequenceRedisUUIDList.forEach(sequenceRedisUUID -> {
					List<RequestHistoryVO> requestHistoryVOList = new ArrayList<>();
					String redisKey = RedisUtil.generateUserSequenceHistoryGetUUIDKey(userPO.getId(), sequenceRedisUUID);
					List<String> historyList = RedisUtil.getList(redisKey);
					if (historyList.size() > 0) {
						historyList.forEach(history -> {
							RequestHistoryVO requestHistoryVO;
							requestHistoryVO = JSON.parseObject(history, RequestHistoryVO.class);
							requestHistoryVOList.add(requestHistoryVO);
						});
					}
					requestHistoryVOListList.add(requestHistoryVOList);
				});
				log.info("【GetRequestHistoryBySequenceID】用户{}获取序列{}的请求历史成功！", userPO.getUserName(), getSequenceHistoryPageDTO.getId());
				return new PageResult<>(pageInfo.getTotal(), requestHistoryVOListList);
			} finally {
				SyncUtil.finish(getSequenceHistoryPageDTO);
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public void deleteSequenceHistory(List<SequenceHistoryOwnerBO> sequenceHistoryOwnerBOList) {
		if (sequenceHistoryOwnerBOList.size() > 0) {
			sequenceHistoryOwnerBOList.forEach(historySequencePO -> {
				String redisKey = RedisUtil.generateUserSequenceHistoryGetUUIDKey(historySequencePO.getSequenceOwnerId(), historySequencePO.getSequenceRedisUuid());
				RedisUtil.remove(redisKey);
			});
			try {
				Collection<String> collection = sequenceHistoryOwnerBOList.stream().map(SequenceHistoryOwnerBO::getSequenceRedisUuid).toList();
				collection = collection.stream().distinct().collect(Collectors.toList());
				historySequenceMapper.deleteBySequenceRedisUuidIn(collection);
			} catch (Exception e) {
				e.printStackTrace();
				throw new BusinessException(BusinessErrorEnum.DATABASE_DELETE_FAILED);
			}
		}
	}

	@Override
	public void deleteSequenceHistoryBySequenceID(DeleteSequenceHistoryDTO deleteSequenceHistoryDTO, UserPO userPO) {
		if(SyncUtil.start(deleteSequenceHistoryDTO+"deleteSequenceHistoryBySequenceID")){
			try {
				if(deleteSequenceHistoryDTO.getId() == null) {
					throw new BusinessException(BusinessErrorEnum.MISSING_REQUIRED_PARAMETERS);
				}
				log.info("【DeleteSequenceHistoryBySequenceID】用户{}正在删除序列{}的请求历史！", userPO.getUserName(), deleteSequenceHistoryDTO.getId());
				List<SequenceHistoryOwnerBO> sequenceHistoryOwnerBOList = new ArrayList<>();
				SequencePO sequencePO;
				try {
					sequencePO = sequenceMapper.selectByPrimaryKey(deleteSequenceHistoryDTO.getId());
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.DATABASE_QUERY_FAILED);
				}
				if (sequencePO == null || !sequencePO.getSequenceOwnerId().equals(userPO.getId())) {
					throw new BusinessException(BusinessErrorEnum.SEQUENCE_NOT_EXIST);
				}
				List<String> uuids;
				try {
					uuids = historySequenceMapper.selectSequenceRedisUuidBySequenceId(deleteSequenceHistoryDTO.getId());
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(BusinessErrorEnum.DATABASE_QUERY_FAILED);
				}
				uuids.forEach(uuid -> {
					SequenceHistoryOwnerBO sequenceHistoryOwnerBO = new SequenceHistoryOwnerBO();
					sequenceHistoryOwnerBO.setSequenceId(deleteSequenceHistoryDTO.getId());
					sequenceHistoryOwnerBO.setSequenceRedisUuid(uuid);
					sequenceHistoryOwnerBO.setSequenceOwnerId(userPO.getId());
					sequenceHistoryOwnerBOList.add(sequenceHistoryOwnerBO);
				});
				deleteSequenceHistory(sequenceHistoryOwnerBOList);
				log.info("【DeleteSequenceHistoryBySequenceID】用户{}删除序列{}的请求历史成功！", userPO.getUserName(), deleteSequenceHistoryDTO.getId());
			} finally {
				SyncUtil.finish(deleteSequenceHistoryDTO+"deleteSequenceHistoryBySequenceID");
			}
		} else {
			throw new BusinessException(BusinessErrorEnum.REQUEST_IS_HANDLING);
		}
	}

	@Override
	public boolean checkSequenceCoolDown(SequencePO sequencePO) {
		String coolDownKey = RedisUtil.generateUserSequenceSendCoolDownKey(sequencePO.getSequenceOwnerId(), sequencePO.getId());
		if (RedisUtil.get(coolDownKey) != null) {
			return true;
		}
		try {
			RedisUtil.put(coolDownKey, "CoolDown", sequencePO.getSequenceRepeatTime(), RedisUtil.convertStringToTimeUnit(sequencePO.getSequenceRepeatTimeUnit()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(BusinessErrorEnum.REDIS_SAVE_FAILED);
		}
		return false;
	}

}