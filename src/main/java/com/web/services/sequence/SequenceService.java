package com.web.services.sequence;

import com.web.base.entity.PageResult;
import com.web.pojo.BO.sequence.SequenceHistoryOwnerBO;
import com.web.pojo.DTO.page.PageDTO;
import com.web.pojo.DTO.sequence.*;
import com.web.pojo.PO.sequence.SequencePO;
import com.web.pojo.PO.user.UserPO;
import com.web.pojo.VO.request.RequestHistoryVO;
import com.web.pojo.VO.sequence.SequenceVO;

import java.util.List;

public interface SequenceService {
	void createSequence(SequenceDTO sequenceDTO, UserPO userPO);

	void updateSequence(UpdateSequenceDTO updateSequenceDTO, UserPO userPO);

	void deleteSequence(DeleteSequenceDTO deleteSequenceDTO, UserPO userPO);

	List<SequenceVO> getSequences(UserPO userPO);

	void switchSequenceEnabled(SequenceEnabledDTO sequenceEnabledDTO, UserPO userPO);

	PageResult<List<RequestHistoryVO>> getRequestHistory(PageDTO pageDTO, UserPO userPO);

	SequenceVO getSequence(GetSequenceDTO getSequenceDTO, UserPO userPO);

	PageResult<List<RequestHistoryVO>> getRequestHistoryBySequenceID(GetSequenceHistoryPageDTO getSequenceHistoryPageDTO, UserPO userPO);

	void deleteSequenceHistory(List<SequenceHistoryOwnerBO> sequenceHistoryOwnerBOList);

	void deleteSequenceHistoryBySequenceID(DeleteSequenceHistoryDTO deleteSequenceHistoryDTO, UserPO userPO);

	boolean checkSequenceCoolDown(SequencePO sequencePO);
}
