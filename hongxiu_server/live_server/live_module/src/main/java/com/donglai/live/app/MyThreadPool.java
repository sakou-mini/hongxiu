package com.donglai.live.app;

import com.donglai.common.contxet.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class MyThreadPool {
    @Bean(name = "mainThreadPool")
    public ExecutorService createMainThreadPool() {
        return new MyThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(), Executors.defaultThreadFactory(), new CustomRejectedExecutionHandler());
    }

    public static ExecutorService getMainThread() {
        return (ExecutorService) SpringApplicationContext.getBean("mainThreadPool");
    }

    @Bean("bgThreadPool")
    public ExecutorService createbgThreadPool() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        return new MyThreadPoolExecutor(availableProcessors, availableProcessors, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    public static ExecutorService getbgThread() {
        return (ExecutorService) SpringApplicationContext.getBean("bgThreadPool");
    }
}

/**
 * 自定义阻塞型线程池 当池满时会阻塞任务提交
 */
class CustomRejectedExecutionHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            // 核心改造点，由blockingqueue的offer改成put阻塞方法
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

@Slf4j
class MyThreadPoolExecutor extends ThreadPoolExecutor {
    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t != null) {
            log.error("任务执行异常:", t);
        }
    }
}

