package com.donglai.common.util;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static final long DAY_MILLISECOND = 1000 * 3600 * 24;
    public static final long WEEK_MILLISECOND = 1000 * 3600 * 24*7;

    private TimeUtil() {
    }

    //获取当月第一天开始时间
    public static long getFirstDayOfCurrentMonth() {
        return LocalDateTime.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .toLocalDate()
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }
    //当日开始时间
    public static long getCurrentDayStartTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }
    // 获取本周第一天开始时间
    public static long getFirstDayOfCurrentWeeks() {
        return LocalDateTime.now().with(DayOfWeek.MONDAY)
                .toLocalDate()
                .atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli();
    }

    public static long getBeforeDayStartTime(int day) {
        long milli = System.currentTimeMillis();
        long time = milli - TimeUnit.DAYS.toMillis(day);
        LocalDate localDate = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MIN);
        return endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long todayStartTime(long nowTime) {
        return nowTime - (nowTime + TimeZone.getDefault().getRawOffset()) % DAY_MILLISECOND;
    }

    public static long getDayEndTime(long nowTime) {
        Long timeDayStartTime = getTimeDayStartTime(nowTime);
        return (timeDayStartTime + DAY_MILLISECOND-1);
    }

    public static long yesterdayStartTime(long nowTime) {
        return nowTime - DAY_MILLISECOND;
    }
    public static long mondayStartTime(long nowTime) {
        return nowTime - WEEK_MILLISECOND;
    }
    public static Long getTimeDayStartTime(Long time)
    {
        LocalDate localDate =Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate();;
        LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MIN);
        return endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static List<Long> getDayTimes(long days,int hour){
        List<Long> times = new ArrayList<>();
        long startTime = todayStartTime(System.currentTimeMillis());
        startTime += TimeUnit.HOURS.toMillis(hour);
        times.add(startTime);
        if(Math.abs(days) >0){
            for (int day = 1; day < Math.abs(days); day++) {
                if(days>0)
                    times.add(startTime + TimeUnit.DAYS.toMillis(day));
                else
                    times.add(startTime - TimeUnit.DAYS.toMillis(day));
            }
        }
        Collections.sort(times);
        return times;
    }

    public static long getMonthOfStartTime(long time){
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime dateTime =LocalDateTime.ofInstant(Instant.ofEpochMilli(time), zone);
        return dateTime.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long getBeforeMonthStartTime(int monthBefore){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -monthBefore);
        long monthOfTime = calendar.getTimeInMillis();
        return getMonthOfStartTime(monthOfTime);
    }


    public static boolean isLastDayOfMonth(long time){
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) + 1));
        return calendar.get(Calendar.DAY_OF_MONTH) == 1;
    }

    public static int getDayOfMonth(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static List<Long> getDayTimeBetweenTimes(long startTime , long endTime){
        if(startTime > endTime) return new ArrayList<>(0);
        List<Long> dayTime = new ArrayList<>();
        startTime = getTimeDayStartTime(startTime);
        endTime =  getTimeDayStartTime(endTime);
        long day = TimeUnit.MILLISECONDS.toDays(endTime - startTime);
        for (int i = 0; i <= day; i++) {
            dayTime.add(startTime + TimeUnit.DAYS.toMillis(i));
        }
        return dayTime;
    }

    public static Long getTimeDayEndTime(Long time) {
        LocalDate date = timeToLocalDate(time);
        LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
        return endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private static LocalDate timeToLocalDate(long time) {
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
