package com.donglai.account.util;

import com.alibaba.fastjson.JSONObject;
import com.donglai.account.BaseTest;
import com.donglai.common.util.HashUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class HttpUtilTest extends BaseTest {
    @Test
    public void duoCaiApiTest() {
        Map<String, String> header = new HashMap<>();
        header.put("Api-Key", "eccbc87e4b5ce2fe28308fd9f2a7baf3");
        Map<String, Object> param = new HashMap<>();
        param.put("username", "lttest_1");
        param.put("password", HashUtils.getMd5Hash("lttest_1"));
        JSONObject jsonObject = HttpUtil.postFormData(header, param, "https://manycai01.net/api/lt/userinfo");
    }
}
