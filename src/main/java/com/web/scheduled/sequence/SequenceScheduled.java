package com.web.scheduled.sequence;

import cn.hutool.log.Log;
import com.web.base.enums.BusinessErrorEnum;
import com.web.base.exceptions.BusinessException;
import com.web.mapper.sequence.HistorySequenceMapper;
import com.web.pojo.BO.sequence.SequenceHistoryOwnerBO;
import com.web.services.sequence.SequenceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
public class SequenceScheduled {
	@Resource
	HistorySequenceMapper historySequenceMapper;

	@Resource
	SequenceService sequenceService;

	private final static Log log = Log.get();

	@Scheduled(cron = "0 0/20 * * * ?")
	public void autoCleanRequestHistory() {
		log.info("【RequestScheduled】自动清理序列历史服务准备启动");
		List<SequenceHistoryOwnerBO> sequenceHistoryOwnerBOList;
		//七天前的date
		Date maxCreateTime = new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000);
		try {
			sequenceHistoryOwnerBOList = historySequenceMapper.selectSequenceHistoryOwnerBOByCreateTimeLessThan(maxCreateTime);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(BusinessErrorEnum.DATABASE_QUERY_FAILED);
		}
		sequenceService.deleteSequenceHistory(sequenceHistoryOwnerBOList);
		log.info("【RequestScheduled】自动清理序列历史服务完成");
	}
}