package com.web.mapper.sequence;

import com.web.pojo.BO.sequence.SequenceRequestsListBO;
import com.web.pojo.PO.sequence.SequencePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SequenceMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(SequencePO record);

	int insertSelective(SequencePO record);

	SequencePO selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(SequencePO record);

	int updateByPrimaryKey(SequencePO record);

	List<SequencePO> selectAllBySequenceOwnerId(@Param("sequenceOwnerId") Integer sequenceOwnerId);

	Integer countBySequenceOwnerId(@Param("sequenceOwnerId") Integer sequenceOwnerId);

	int deleteById(@Param("id") Integer id);

	List<Boolean> selectIsCookieInheritBySequenceOwnerId(@Param("sequenceOwnerId") Integer sequenceOwnerId);

	List<SequencePO> selectAllBySequenceEnabled(@Param("sequenceEnabled") Boolean sequenceEnabled);

	int updateSequenceEnabledById(@Param("updatedSequenceEnabled") Boolean updatedSequenceEnabled, @Param("id") Integer id);

	List<SequenceRequestsListBO> selectSequenceRequestsBySequenceOwnerId(@Param("sequenceOwnerId") Integer sequenceOwnerId);

	List<String> selectSequenceHistoryBySequenceOwnerId(@Param("sequenceOwnerId") Integer sequenceOwnerId);

	List<SequencePO> selectAllByIsCookieInherit(@Param("isCookieInherit")Boolean isCookieInherit);


}