package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.builder.QPlatformGameConfigBuilder;
import com.donglaistd.jinli.database.dao.system.GiftConfigDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.system.GiftConfig;
import com.donglaistd.jinli.http.entity.GiftSendInfo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import static com.donglaistd.jinli.constant.LiveUserConstant.DefaultQuickChat;

public class HttpUtilTest extends BaseTest {
    @Autowired
    HttpUtil httpUtil;

    @Test
    public void testConnected(){
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("www.jinli88.net", 8080), 2000);
            System.err.println("连接成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void test() {
        String url = "https://h5.jinli88.net/share/giftJson";
        Assert.assertTrue(httpUtil.verifyHostIsAvailable(url));
    }

    @Autowired
    QPlatformGameConfigBuilder qPlatformGameConfigBuilder;
    @Autowired
    GiftConfigDaoService giftConfigDaoService;

    @Test
    public void RechargeTest(){
        giftConfigDaoService.initGiftConfig();
        //String accountName = "535710239";
        String accountName = "3907767532";
        GiftConfig giftConfig = giftConfigDaoService.findByGiftIdAndPlatform("10008", Constant.PlatformType.PLATFORM_JINLI);
        GiftSendInfo sendInfo = new GiftSendInfo(giftConfig, 1, user);
        sendInfo.setGiftOfLiveUserId("263476");
        sendInfo.setGiftOfLiveUserName("欲问酒家何处有");
        boolean b = httpUtil.requestRewardForPlatformQ(accountName, "lhd", 1, sendInfo,  OrderIdUtils.getOrderNumber());
        System.out.println(b);
    }
}
