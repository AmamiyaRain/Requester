package com.web.scheduled.request;

import cn.hutool.log.Log;
import com.web.mapper.sequence.SequenceMapper;
import com.web.pojo.PO.sequence.SequencePO;
import com.web.services.request.RequestService;
import com.web.services.sequence.SequenceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class RequestScheduled {
	@Resource
	private SequenceMapper sequenceMapper;

	@Resource
	private RequestService requestService;

	@Resource
	private SequenceService sequenceService;

	private final static Log log = Log.get();

	//五分钟执行一次
	@Scheduled(cron = "0 0/5 * * * ?")
	public void sendRequest() {
		log.info("【RequestScheduled】自动序列发送服务准备启动");
		List<SequencePO> sequencePOList = sequenceMapper.selectAllBySequenceEnabled(true);
		for (SequencePO sequencePO : sequencePOList) {
			if (sequencePO.getSequenceRequests().size() > 0) {
				if (!sequenceService.checkSequenceCoolDown(sequencePO)) {
					requestService.sendRequestBySequences(sequencePO);
				}
			}
		}
		log.info("【RequestScheduled】自动序列发送服务完成");
	}
}