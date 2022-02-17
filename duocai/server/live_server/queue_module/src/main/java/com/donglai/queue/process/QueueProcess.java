package com.donglai.queue.process;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.protocol.Common;
import com.donglai.protocol.HongXiu;
import com.donglai.queue.db.service.QueueExecuteService;
import com.donglai.queue.message.producer.Producer;
import com.donglai.queue.timewheel.Timer;
import com.donglai.queue.timewheel.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class QueueProcess
{

	public static ConcurrentHashMap<String,Integer> idMap = new ConcurrentHashMap<>();
	private static Timer timer; // 时间轮

	// 服務器初始化
	public static void init()
	{
		// 初始化時間輪
		timer = new Timer();
		// 導入初始數據
		List<QueueExecute> values = SpringApplicationContext.getBean(QueueExecuteService.class).findAll();
		if (values == null)
			return;
		// 數據加入時間輪任務
		values.forEach(QueueProcess::addTask);
	}

	// 增加時間輪任務
	public static void addTask(QueueExecute v)
	{
		//延迟值
		long delay = v.getEndTime() - System.currentTimeMillis();
		TimerTask timerTask = new TimerTask(delay, () ->
		{
			log.info(v.getId() + "----------执行");
			//消息发送至kafka
			SpringApplicationContext.getBean(Producer.class).send(v, getExecuteFinishPb(v));
		});
		timer.addTask(timerTask);
	}

	// 增加時間輪任務
	public static void addTask(String id)
	{
		QueueExecute v = SpringApplicationContext.getBean(QueueExecuteService.class).findById(id);
		if (v == null)
		{
			idMap.put(id, 0);
			log.error("未找到执行队列 {} ,延后重新查找 ！",id);
			return;
		}
		addTask(v);
	}

	/*執行隊列完成build*/
	public static HongXiu.HongXiuMessageRequest getExecuteFinishPb(QueueExecute queueExecute)
	{
		//TODO if has other server ，should judge MessageRequest
		switch (queueExecute.getFromServer()){
			case QueueExecute.LIVE:
				/*1.构造返回的Live Builder*/
				return HongXiu.HongXiuMessageRequest.newBuilder().setLiveOfQueueFinishRequest(Common.LiveOfQueueFinishRequest.newBuilder().setQueueId(queueExecute.getId())).build();
			case QueueExecute.ACCOUNT:
				/*12构造返回的Account Builder*/
				return HongXiu.HongXiuMessageRequest.newBuilder().setAccountOfQueueFinishRequest(Common.AccountOfQueueFinishRequest.newBuilder().setQueueId(queueExecute.getId())).build();
			default:
				return null;
		}
	}
}
