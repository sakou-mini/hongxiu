package com.donglaistd.jinli.task.timewheel;

/**
 * 任务
 */
public class TimerTask {

	// 描述
	public String desc;
	// 时间槽
	protected TimerTaskList timerTaskList;
	// 下一个节点
	protected TimerTask next;
	// 上一个节点
	protected TimerTask pre;
	// 延迟时间
	private long delayMs;
	// 任务
	private Runnable task;

	public TimerTask(long delayMs, Runnable task) {
		this.delayMs = System.currentTimeMillis() + delayMs;
		this.task = task;
		this.timerTaskList = null;
		this.next = null;
		this.pre = null;
	}

	public Runnable getTask() {
		return task;
	}

	public long getDelayMs() {
		return delayMs;
	}

	@Override
	public String toString() {
		return desc;
	}
}
