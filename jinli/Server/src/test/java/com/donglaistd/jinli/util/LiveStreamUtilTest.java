package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.LiveStreamUrlType;
import com.donglaistd.jinli.util.live.LiveStream;
import com.donglaistd.jinli.util.live.LiveStreamFactory;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;


@RunWith(SpringRunner.class)
@SpringBootTest
public class LiveStreamUtilTest {

    @Test
    public void testTc() {
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(Constant.LiveSourceLine.TENCENT_LINE);
        //

        //qianxj.com,zhenjianghuishou.com,fysjpj.com,jqssz88.com, junmingyu.com,pmziyuanku.com,daojiao19.com,dolphinsturgeon.com
        ArrayList<String> domains = Lists.newArrayList("qianxj.com", "zhenjianghuishou.com", "fysjpj.com", "jqssz88.com", "junmingyu.com", "pmziyuanku.com", "daojiao19.com", "dolphinsturgeon.com");
        for (String domain : domains) {
            String liveUrl = "832a1a12315348b9a1015302a600fd70";
            String pushLiveUrl = liveStream.getRtmpPushUrl(domain,liveUrl);
            System.out.println("腾讯 pushAddress is: " + pushLiveUrl);
            String pullLiveUrl = liveStream.getRtmpPullUrl(domain,liveUrl);
            System.out.println("腾讯 pullAddress is: " + pullLiveUrl);
            String m3u8 = liveStream.getFullHttpLiveUrlByType(domain,liveUrl, LiveStreamUrlType.m3u8);
            String flv = liveStream.getFullHttpLiveUrlByType(domain,liveUrl, LiveStreamUrlType.flv);
            System.out.println(m3u8);
            System.out.println(flv);
            System.out.println("============================================================");

            //不可用： pmziyuanku.com daojiao19.com dolphinsturgeon.com
        }
    }

    @Test
    public void testAli() {
        String domain = "sjzooo.com";
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(Constant.LiveSourceLine.ALIYUN_LINE);
        String liveUrl = "6973009cda0b4f0bba671c6b2a458e4f";
        String pushLiveUrl = liveStream.getRtmpPushUrl(domain, liveUrl);
        System.out.println("阿里 pushAddress is: " + pushLiveUrl);
        String pullLiveUrl = liveStream.getRtmpPullUrl(domain, liveUrl);
        System.out.println("阿里 pullAddress is: " + pullLiveUrl);
        String m3u8 = liveStream.getFullHttpLiveUrlByType(domain, liveUrl, LiveStreamUrlType.m3u8);
        String flv = liveStream.getFullHttpLiveUrlByType(domain, liveUrl, LiveStreamUrlType.flv);
        System.out.println(m3u8);
        System.out.println(flv);
    }

    @Test
    public void testWangsu(){
        ArrayList<String> domains = Lists.newArrayList("sdsksc.com", "szdhcc.com", "szhdns.com", "bfszhl.com", "bjlhyt.com", "sjzooo.com", "sjzvvv.com", "sdfvy.bar");
        for (String domain : domains) {
            LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(Constant.LiveSourceLine.WANGSU_LINE);
            String liveUrl = "demo15";
            assert liveStream != null;
            String rtmpPushUrl = liveStream.getRtmpPushUrl(domain,liveUrl);
            String rtmpPullUrl = liveStream.getRtmpPullUrl(domain,liveUrl);
            System.out.println("网宿 pushAddress is: " +rtmpPushUrl);
            System.out.println("网宿 pullAddress is: " +rtmpPullUrl);
            String m3u8 = liveStream.getFullHttpLiveUrlByType(domain, liveUrl, LiveStreamUrlType.m3u8);
            String flv = liveStream.getFullHttpLiveUrlByType(domain, liveUrl, LiveStreamUrlType.flv);
            System.out.println("hls 拉流："+m3u8);
            System.out.println("flv 拉流："+flv);
        }
    }

}