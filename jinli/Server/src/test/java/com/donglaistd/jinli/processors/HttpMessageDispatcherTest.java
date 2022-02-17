package com.donglaistd.jinli.processors;

import com.donglaistd.jinli.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class HttpMessageDispatcherTest extends BaseTest {
    @Autowired
    HttpMessageDispatcher httpMessageDispatcher;

    @Test
    public void test(){
        String request = "{\n" +
                "    \"messageId\":1000,\n" +
                "    \"request\":{\"roomId\":56}\n" +
                "}";
        httpMessageDispatcher.dispatch(context, request);
    }
}
