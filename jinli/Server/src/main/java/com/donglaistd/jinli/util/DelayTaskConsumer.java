package com.donglaistd.jinli.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class DelayTaskConsumer {

    private final Logger logger = Logger.getLogger(DelayTaskConsumer.class.getName());

    private final LinkedBlockingQueue<Runnable> methodsQueue = new LinkedBlockingQueue<>();

    protected FutureTaskWeakSet futureTaskWeakSet = new FutureTaskWeakSet();

    private  Runnable runAfter;

    private DelayTaskConsumer() {
    }

    public static DelayTaskConsumer newInstance() {
        return new DelayTaskConsumer();
    }

    public void addExecMethod(Runnable runnable){
        methodsQueue.add(runnable);
    }

    public synchronized void startRun(long delay){
        if(!futureTaskWeakSet.getFutureSet().isEmpty()) {
            return;
        }
        consumeQueue(delay);
    }

    private synchronized void consumeQueue(long delay){
        if(methodsQueue.isEmpty()) {
            if(runAfter!=null){
                runAfter.run();
            }
            cleanQueueTask();
            logger.warning(Thread.currentThread().getName()+ "------->delay queue is finish");
            return;
        }
        Runnable take = methodsQueue.poll();
        take.run();
        futureTaskWeakSet.add(ScheduledTaskUtil.schedule(()->consumeQueue(delay), delay));
    }

    public void setRunAfter(Runnable runAfter) {
        this.runAfter = runAfter;
    }

    public synchronized void cleanQueueTask(){
        futureTaskWeakSet.clear();
        futureTaskWeakSet.getFutureSet().clear();
        this.methodsQueue.clear();
        this.runAfter = null;
    }

    public LinkedBlockingQueue<Runnable> getMethodsQueue() {
        return methodsQueue;
    }
}
