package com.donglai.test.message;

import com.donglai.test.TestApp;
import com.donglai.test.netty.HttpClientNetty;
import com.donglai.test.netty.WebSocketClientHandler;
import com.donglai.test.util.MessageUtil;
import io.netty.channel.Channel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApp.class)
public class LiveTest {
    @Test
    public void test() throws InterruptedException {
        //1.get channel
        WebSocketClientHandler webSocketClientHandler = new WebSocketClientHandler();
        HttpClientNetty httpClientNetty = new HttpClientNetty("127.0.0.1", 7005);
        Channel channel = httpClientNetty.createConnected(webSocketClientHandler);
        MessageUtil.regist(channel);
        Thread.sleep(3000);
        MessageUtil.login(channel);
        //MessageUtil.login(channel, "lttest_1", "lttest_1");
        Thread.sleep(3000);
        // MessageUtil.updatePassword(channel);
        // Thread.sleep(3000);
        //MessageUtil.applyLiveUser(channel);
        //Thread.sleep(3000);
        //MessageUtil.queryLiveUserInfo(channel);
        //Thread.sleep(3000);
        //MessageUtil.startLiveRequest(channel);
        //Thread.sleep(3000);
        //UserCache userCache = DataManager.getUserCache(channel);
        //MessageUtil.enterRoomLiveRequest(channel,userCache.getRoomId());
        //
        //Thread.sleep(10000);
        //MessageUtil.endLiveRequest(channel);
        //Thread.sleep(10000);
    }

    @Test
    public void test2() throws InterruptedException {
        //1.get channel
        WebSocketClientHandler webSocketClientHandler = new WebSocketClientHandler();
        HttpClientNetty httpClientNetty = new HttpClientNetty("127.0.0.1", 7005);
        Channel channel = httpClientNetty.createConnected(webSocketClientHandler);
        MessageUtil.thirdPartySignUp(channel);
        Thread.sleep(3000);
        MessageUtil.login(channel);
        Thread.sleep(3000);
    }
}
