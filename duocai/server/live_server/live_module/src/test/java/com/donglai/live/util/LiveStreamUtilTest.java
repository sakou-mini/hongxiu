package com.donglai.live.util;

import com.donglai.common.util.live.LiveStream;
import com.donglai.common.util.live.LiveStreamFactory;
import com.donglai.live.BaseTest;
import org.junit.Test;

import static com.donglai.common.constant.LineSourceConstant.TENCENT_LINE;

public class LiveStreamUtilTest extends BaseTest {

    @Test
    public void test(){
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(TENCENT_LINE);
        String liveDomain = "zhenjianghuishou.com";
        String liveUrl = "4f092088556a4f83bb1497d81bc96a0a";
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
