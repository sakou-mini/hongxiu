package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.Jinli;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.GameConstant.DAY_OF_MAX_HOUR;

public class TimeUtilTest extends BaseTest {
    Logger logger = Logger.getLogger(TimeUtilTest.class.getName());

    @Test
    public void test(){
        long beforeMonthStartTime = TimeUtil.getBeforeMonthStartTime(12);
        System.out.println(beforeMonthStartTime);
        //获取期间的所有月份
        List<Long> month = getBeforeMonthStartTimeList(12);
        Assert.assertEquals(12,month.size());
    }

    public List<Long> getBeforeMonthStartTimeList (int month){
        List<Long> monthStartTime = new ArrayList<>(month);
        for (int i = month; i > 0; i--) {
            monthStartTime.add(TimeUtil.getBeforeMonthStartTime(i));
        }
        SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd : HH:mm:ss");
        monthStartTime.forEach(time-> System.out.println(smf.format(new Date(time))));
        return monthStartTime;
    }

    @Test
    public void testget(){
        long time = 1614504616000L;
        boolean lastDayOfMonth = TimeUtil.isLastDayOfMonth(time);
        Assert.assertTrue(lastDayOfMonth);
    }

    @Test
    public void serviceFeeTest(){
        int serviceFee = getServiceFee(200, 1150, BigDecimal.valueOf(0.95));
       Assert.assertEquals(50,serviceFee);
    }

    public int getServiceFee(long betAmount, long winOrLose, BigDecimal payRate){
        if(winOrLose <=0 || betAmount <=0) return 0;
        BigDecimal payRateBeforeAmount = ((BigDecimal.valueOf(winOrLose).subtract(BigDecimal.valueOf(betAmount)).divide(payRate, 4, RoundingMode.HALF_UP)));
        return payRateBeforeAmount.add(BigDecimal.valueOf(betAmount)).subtract(BigDecimal.valueOf(winOrLose)) .intValue();
    }

    @Test
    public void getDayTimeBetweenTimeTest(){
        long startTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3);
        List<Long> days = TimeUtil.getDayTimeBetweenTimes(startTime, System.currentTimeMillis());
        Assert.assertEquals(4, days.size());
        Assert.assertEquals(TimeUtil.getCurrentDayStartTime(), days.get(3).longValue());
    }

    @Test
    public void messageTest(){
        Jinli.PlayCardsForLandlordRequest.Builder playCardBuilder = Jinli.PlayCardsForLandlordRequest.newBuilder();
        Jinli.PlayCardsForLandlordRequest build = playCardBuilder.build();
        System.out.println(Jinli.JinliMessageRequest.class.getName().split("\\$")[0]);
        List<Descriptors.FieldDescriptor> fields = Jinli.JinliMessageRequest.getDescriptor().getFields();
        System.out.println(Jinli.JinliMessageRequest.class.getPackageName());
        for (Descriptors.FieldDescriptor field : fields) {
            Descriptors.Descriptor messageType = field.getMessageType();
            System.out.println(field.getMessageType().getFullName()+":" + field.getNumber());
        }

    }

    @Test
    public void getDayOfHourTest(){
        String strHour = "0:00";
        System.out.println(TimeUtil.strHourToNumber(strHour));
        strHour = "1:00";
        System.out.println(TimeUtil.strHourToNumber(strHour));
        strHour = "2:00";
        System.out.println(TimeUtil.strHourToNumber(strHour));
        strHour = "3:00";
        System.out.println(TimeUtil.strHourToNumber(strHour));
    }

    public String formatHourToStrHourRange(int hour){
        final String CLOCK_SLICER = ":00";
        int startHour = hour;
        int endHour = hour+1;
        if(hour >= DAY_OF_MAX_HOUR){
            endHour = 0;
        }
        return String.format("%s%s-%s%s",startHour,CLOCK_SLICER,endHour,CLOCK_SLICER);
    }

    @Test
    public void hourNumberToStrHourRange(){
        for (int i = 0; i <=23 ; i++) {
            System.out.println(i +"----------->"+  formatHourToStrHourRange(i));
        }
    }

    @Test
    public void getTimeByHour(){
        long time = TimeUtil.getTimeByHour(5);
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String formatDate = sm.format(new Date(time));
        System.out.println(formatDate);


        time = TimeUtil.getTimeByHour(22);
        formatDate = sm.format(new Date(time));
        System.out.println(formatDate);

        time = TimeUtil.getTimeByHour(23);
        formatDate = sm.format(new Date(time));
        System.out.println(formatDate);

        time = TimeUtil.getTimeByHour(0);
        formatDate = sm.format(new Date(time));
        System.out.println(formatDate);
    }

}
