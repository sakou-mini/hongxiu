package com.donglai.live.process;

import com.donglai.common.constant.QueueType;
import com.donglai.live.message.producer.Producer;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.model.entityBuilder.QueueBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.donglai.common.constant.QueueType.*;
import static com.donglai.live.constant.Constant.GIFT_CONTRIBUTE_INCOME_RANK_ID;

@Component
@Slf4j
public class QueueProcess {
    @Value("${gift.rank.update.time}")
    private long giftRankUpdateIntervalTime;
    @Value("${sport.event.interval.time}")
    public int sportRaceIntervalTime;

    @Autowired
    QueueExecuteService queueExecuteService;
    @Autowired
    Producer producer;

    //创建延迟关闭直播队列
    public QueueExecute createAndSendEndLiveQueue(Room room, long delayTime) {
        long endTime = System.currentTimeMillis() + delayTime;
        QueueExecute queue = QueueBuilder.createQueue(room.getUserId(), room.getId(), endTime, QueueType.END_LIVE, QueueExecute.LIVE);
        queue = queueExecuteService.save(queue);
        producer.sendQueue(queue.getId());
        return queue;
    }

    //创建自动关闭直播队列
    public QueueExecute createAndSendAutoEndLiveQueue(Room room, long endTime) {
        QueueExecute queue = QueueBuilder.createQueue(room.getUserId(), room.getId(), endTime, QueueType.TIMEOUT_END_LIVE, QueueExecute.LIVE);
        queue = queueExecuteService.save(queue);
        producer.sendQueue(queue.getId());
        return queue;
    }

    //初始化赛事列表更新，每5分钟更新一次
    public void initSportEventListQueue(){
        QueueExecute sportEventQueue = queueExecuteService.findByQueueTypeAndRefId(SPORT_EVENT.getValue(), SPORT_EVENT.name());
        if (sportEventQueue == null) {
            long endTime = System.currentTimeMillis() + sportRaceIntervalTime;
            sportEventQueue = QueueBuilder.createQueue(endTime, QueueType.SPORT_EVENT,  QueueType.SPORT_EVENT.name(), QueueExecute.LIVE);
            sportEventQueue = queueExecuteService.save(sportEventQueue);
            producer.sendQueue(sportEventQueue.getId());
            log.info("init task to update sport event rank {}", sportEventQueue.getId());
        } else {
            log.info("已经初始化了 体育赛事 queue {}", sportEventQueue.getId());
        }
    }


    //初始化创建礼物排行更新队列
    public void initRankQueue() {
        QueueExecute giftRankKey = queueExecuteService.findByQueueTypeAndRefId(GIFT_CONTRIBUTE_INCOME_RANK.getValue(), GIFT_CONTRIBUTE_INCOME_RANK_ID);
        if (giftRankKey == null) {
            long endTime = System.currentTimeMillis() + giftRankUpdateIntervalTime;
            giftRankKey = QueueBuilder.createQueue(endTime, GIFT_CONTRIBUTE_INCOME_RANK, GIFT_CONTRIBUTE_INCOME_RANK_ID,QueueExecute.LIVE);
            queueExecuteService.save(giftRankKey);
            producer.sendQueue(giftRankKey.getId());
            log.info("init task to update gift rank {}", giftRankKey.getId());
        } else {
            log.info("已经初始化了 排行queue {}", giftRankKey.getId());
        }
    }

    //初始化每分钟job
    public void initMinuteJobQueue() {
        QueueExecute queueExecute = queueExecuteService.findByQueueTypeAndRefId(LIVE_MINUTE_JOB.getValue(), LIVE_MINUTE_JOB.name());
        if (queueExecute == null) {
            long endTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);
            queueExecute = QueueBuilder.createQueue(endTime, QueueType.LIVE_MINUTE_JOB, QueueType.LIVE_MINUTE_JOB.name(), QueueExecute.LIVE);
            queueExecute = queueExecuteService.save(queueExecute);
            producer.sendQueue(queueExecute.getId());
            log.info("init live_minute_job task {}", queueExecute.getId());
        } else {
            log.info("已经初始化了每分钟 更新队列 queue {}", queueExecute.getId());
        }
    }

}
