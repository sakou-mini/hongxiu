package com.donglai.common.util.live;


public interface LiveStream {
    String getRtmpPushUrl(String domain, String liveUrl,Object ...otherParam);
    String getRtmpPullUrl(String domain, String liveUrl,Object ...otherParam);
    String getFullHttpLiveUrlByType(String domain, String liveUrl, LiveStreamFactory.LiveStreamUrlType liveStreamUrlType);
}
