package com.donglaistd.jinli.util;

import java.util.concurrent.TimeUnit;

public class PeriodicTimer {
    public PeriodicTimer(int period, long delay) {
        setPeriodic(period,delay);
    }

    public PeriodicTimer(int period)
    {
        setPeriodic(period,0);
    }
    public PeriodicTimer(){}

    public synchronized boolean update(long diffNano)
    {
        if(delay>0) {
            delay -= diffNano;
            if(delay>0) return false;
        }
        if(period > 0){
            period -= diffNano;
            System.err.println("剩余倒计时！"+period());
            return period <= 0;
        }
        return false;
    }
    public synchronized void setPeriodic(int periodSecond,long delay)
    {
        this.period = TimeUnit.SECONDS.toNanos(periodSecond);
        this.delay =  TimeUnit.SECONDS.toNanos(delay);
    }

    public int period() {
        return (int) TimeUnit.NANOSECONDS.toSeconds(period);
    }
    private long period;
    private long delay;

}
