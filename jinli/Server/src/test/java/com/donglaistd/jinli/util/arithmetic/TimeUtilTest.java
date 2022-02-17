package com.donglaistd.jinli.util.arithmetic;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.util.TimeUtil;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtilTest extends BaseTest {
    @Test
    public void weekStartTest(){
        long firstDayOfCurrentWeeks = TimeUtil.getFirstDayOfCurrentWeeks();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd:HH:mm:ss");
        String format = simpleDateFormat.format(new Date(firstDayOfCurrentWeeks));
        System.out.println(format);
        System.out.println(firstDayOfCurrentWeeks);
    }
}
