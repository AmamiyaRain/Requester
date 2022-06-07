package com.web.mapper.sequence;

import com.web.pojo.BO.sequence.SequenceHistoryOwnerBO;
import com.web.pojo.PO.sequence.HistorySequencePO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface HistorySequenceMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(HistorySequencePO record);

	int insertSelective(HistorySequencePO record);

	HistorySequencePO selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(HistorySequencePO record);

	int updateByPrimaryKey(HistorySequencePO record);

	String selectSequenceRedisUuidById(@Param("id") Integer id);

	List<String> selectSequenceRedisUuidBySequenceId(@Param("sequenceId") Integer sequenceId);

	List<SequenceHistoryOwnerBO> selectSequenceHistoryOwnerBOByCreateTimeLessThan(@Param("maxCreateTime") Date maxCreateTime);

	int deleteBySequenceIdIn(@Param("sequenceIdCollection") Collection<Integer> sequenceIdCollection);

	int deleteBySequenceRedisUuidIn(@Param("sequenceRedisUuidCollection")Collection<String> sequenceRedisUuidCollection);


}