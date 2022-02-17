package com.donglai.web.util;

import com.donglai.common.util.TimeUtil;
import com.donglai.web.WebBaseTest;
import org.junit.Test;

public class TimeUtilTest extends WebBaseTest {

    @Test
    public void monthBeforeTest(){
        long beforeMonthStartTime = TimeUtil.getBeforeDayStartTime(0);
        System.out.println(beforeMonthStartTime);
    }
}
