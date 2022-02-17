package com.donglaistd.jinli.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskUtil {
    static private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10);

    public static ScheduledFuture<?> schedule(Runnable task, long time) {
        return scheduledThreadPoolExecutor.schedule(task, time, TimeUnit.MILLISECONDS);
    }

    public static <V> ScheduledFuture<V> schedule(Callable<V> task, long time) {
        return scheduledThreadPoolExecutor.schedule(task, time, TimeUnit.MILLISECONDS);
    }
}
