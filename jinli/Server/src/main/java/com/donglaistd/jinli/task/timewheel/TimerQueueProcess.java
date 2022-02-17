package com.donglaistd.jinli.task.timewheel;

import com.donglaistd.jinli.config.SpringContext;
import com.donglaistd.jinli.database.dao.QueueExecuteDaoService;
import com.donglaistd.jinli.database.entity.QueueExecute;
import com.donglaistd.jinli.service.queue.QueueFinishProcess;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class TimerQueueProcess
{
	public static final Logger logger = Logger.getLogger(TimerQueueProcess.class.getName());
	public static ConcurrentHashMap<String,Integer> idMap = new ConcurrentHashMap<>();
	private static Timer timer; // 时间轮

	// 服務器初始化
	public static void init()
	{
		// 初始化時間輪
		timer = new Timer();
		// 導入初始數據
		List<QueueExecute> values = SpringContext.getBean(QueueExecuteDaoService.class).findAll();
		if (values == null)
			return;
		// 數據加入時間輪任務
		values.forEach(TimerQueueProcess::addTask);
	}

	// 增加時間輪任務
	public static void addTask(QueueExecute v)
	{
		//延迟值
		long delay = v.getEndTime() - System.currentTimeMillis();
		TimerTask timerTask = new TimerTask(delay, () ->
		{
			//logger.info(v.getId() + "----------执行");
			//消息发送至kafka
			SpringContext.getBean(QueueFinishProcess.class).process(v.getId());
		});
		timer.addTask(timerTask);
	}

	// 增加時間輪任務
	public static void addTask(String id)
	{
		QueueExecute v = SpringContext.getBean(QueueExecuteDaoService.class).findById(id);
		if (v == null)
		{
			idMap.put(id, 0);
			logger.warning("未找到执行队列 "+id+" 延后重新查找 ！");
			return;
		}
		addTask(v);
	}
}
