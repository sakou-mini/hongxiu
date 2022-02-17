package com.donglaistd.jinli.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyExecutorService {
    private static ExecutorService executorServicePool;

    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    public static  ExecutorService getExecutorServicePool(){
        if(executorServicePool == null)
            executorServicePool = newCachedThreadPool();
        return executorServicePool;
    }
}
