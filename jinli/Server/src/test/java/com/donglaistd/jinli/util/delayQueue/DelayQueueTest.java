package com.donglaistd.jinli.util.delayQueue;

import com.donglaistd.jinli.util.DelayTaskConsumer;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DelayQueueTest {
    Logger logger = Logger.getLogger(DelayQueueTest.class.getName());

    public ExecutorService createThreadPool()
    {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(availableProcessors, availableProcessors, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    @Test
    public void testMethodQueue() throws InterruptedException {
        ExecutorService threadPool = createThreadPool();
        logger.info("start！");
        DelayTaskConsumer queueConsumer = DelayTaskConsumer.newInstance();
        queueConsumer.addExecMethod(()-> task(1,2));
        queueConsumer.addExecMethod(()->  task(1,4));
        queueConsumer.addExecMethod(()->  task(1,7));
        queueConsumer.addExecMethod(()->  logger.info(Thread.currentThread().getName()+"------》task1"));
        queueConsumer.addExecMethod(()->  logger.info(Thread.currentThread().getName()+"------》task2"));
        queueConsumer.addExecMethod(()->  logger.info(Thread.currentThread().getName()+"------》task3！"));
        queueConsumer.addExecMethod(()->  logger.info(Thread.currentThread().getName()+"------》task4"));
        queueConsumer.addExecMethod(()->  logger.info(Thread.currentThread().getName()+"------》task5"));
        queueConsumer.addExecMethod(()->  logger.info(Thread.currentThread().getName()+"------》task6！"));
        queueConsumer.setRunAfter(()-> logger.info(Thread.currentThread().getName()+"------》execute method！"));

        for (int i = 0; i <10 ; i++) {
            threadPool.submit(()->queueConsumer.startRun(1000));
        }
        Thread.sleep(11000);
        Assert.assertEquals(0,queueConsumer.getMethodsQueue().size());
    }

    public void task(int num1,int num2){
        logger.info(Thread.currentThread().getName()+"------》"+ num1+ " + " + num2 + " = " + (num1+num2));
    }
}
