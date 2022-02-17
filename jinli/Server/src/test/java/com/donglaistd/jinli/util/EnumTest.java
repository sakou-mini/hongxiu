package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import org.junit.Test;

public class EnumTest extends BaseTest {
    @Test
    public void test(){
        Constant.DiaryStatue diaryStatue = Constant.DiaryStatue.forNumber(5);
    }
}
