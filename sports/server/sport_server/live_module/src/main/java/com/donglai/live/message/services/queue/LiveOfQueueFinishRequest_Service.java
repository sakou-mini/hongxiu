package com.donglai.live.message.services.queue;

import com.donglai.common.constant.QueueType;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.message.services.queue.handler.EndLiveHandler;
import com.donglai.live.message.services.queue.handler.MinuteJobHandler;
import com.donglai.live.message.services.queue.handler.SportEventHandler;
import com.donglai.live.message.services.queue.handler.UpdateGiftRankHandler;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("LiveOfQueueFinishRequest")
@Slf4j
public class LiveOfQueueFinishRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    private QueueExecuteService queueExecuteService;
    @Autowired
    EndLiveHandler endLiveHandler;
    @Autowired
    UpdateGiftRankHandler updateGiftRankHandler;
    @Autowired
    SportEventHandler sportEventHandler;
    @Autowired
    MinuteJobHandler minuteJobHandler;


    @Override
    public KafkaMessage.TopicMessage Process(String s, HongXiu.HongXiuMessageRequest message, Object... param) {
        var queueRequest = message.getLiveOfQueueFinishRequest();
        String queueId = queueRequest.getQueueId();
        var queueExecute = queueExecuteService.findById(queueId);
        if (queueExecute == null) {
            log.info("队列已失效, queue id is {}", queueId);
            return null;
        }
        log.info("队列的结束时间为：{}", queueExecute.getEndTime());
        switch (QueueType.valueOf(queueExecute.getTriggerType())) {
            case END_LIVE:
            case TIMEOUT_END_LIVE:
                endLiveHandler.deal(queueExecute);
                break;
            case GIFT_CONTRIBUTE_INCOME_RANK:
                updateGiftRankHandler.deal(queueExecute);
                break;
            case SPORT_EVENT:
                sportEventHandler.deal(queueExecute);
                break;
            case LIVE_MINUTE_JOB:
                minuteJobHandler.deal(queueExecute);
                break;
        }
        queueExecuteService.del(queueExecute);
        return null;
    }

    @Override
    public void Close(String s) {

    }
}
