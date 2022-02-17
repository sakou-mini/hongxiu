package com.donglai.live.util;

import com.donglai.common.util.live.LiveStream;
import com.donglai.common.util.live.LiveStreamFactory;
import com.donglai.live.BaseTest;
import org.junit.Test;

import static com.donglai.common.constant.LineSourceConstant.TENCENT_LINE;
import static com.donglai.common.constant.LineSourceConstant.WANGSU_LINE;

public class LiveStreamUtilTest extends BaseTest {

    @Test
    public void test(){
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(WANGSU_LINE);
        String liveDomain = "sjzooo.com";
        String liveUrl = "demo1";
        String rtmpPullUrl = liveStream.getRtmpPullUrl(liveDomain,liveUrl);
        String rtmpPushUrl = liveStream.getRtmpPushUrl(liveDomain,liveUrl);
        System.out.println(rtmpPullUrl);
        System.out.println(rtmpPushUrl);
        String hls = liveStream.getFullHttpLiveUrlByType(liveDomain,liveUrl, LiveStreamFactory.LiveStreamUrlType.m3u8);
        String flv = liveStream.getFullHttpLiveUrlByType(liveDomain,liveUrl, LiveStreamFactory.LiveStreamUrlType.flv);
        System.out.println(hls);
        System.out.println(flv);

    }
}
