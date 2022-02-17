package com.donglai.live.util;

import com.donglai.common.util.TimeUtil;
import com.donglai.live.BaseTest;
import org.junit.Test;

public class TimeUtilTest extends BaseTest {

    @Test
    public void timeTest() {
        Long timeDatEndTime = TimeUtil.getBeforeDayStartTime(-1);
        System.out.println(timeDatEndTime);
    }
}
