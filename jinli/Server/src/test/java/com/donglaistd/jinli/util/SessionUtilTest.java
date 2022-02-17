package com.donglaistd.jinli.util;

import cn.hutool.json.JSONObject;
import com.donglaistd.jinli.BaseTest;
import org.junit.Test;

public class SessionUtilTest extends BaseTest {

    @Test
    public void createSessionAndDecodeSessionTest(){
        String userSession = SessionUtil.getUserSession(user);
        System.out.println(userSession);
        JSONObject jsonObject = SessionUtil.deCodeToJSon(userSession);
        System.out.println(jsonObject.getStr(SessionUtil.KEY_USER_ID));
        System.out.println(jsonObject.getStr(SessionUtil.KEY_TIMES));
        System.out.println(jsonObject.getStr(SessionUtil.KEY_TOKEN));
    }
}
