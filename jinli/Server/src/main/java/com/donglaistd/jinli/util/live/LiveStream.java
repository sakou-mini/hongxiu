package com.donglaistd.jinli.util.live;

import com.donglaistd.jinli.constant.LiveStreamUrlType;

import java.util.List;

public interface LiveStream {
    String getRtmpPushUrl(String domain, String liveUrl,Object ...otherParam);
    String getRtmpPullUrl(String domain, String liveUrl,Object ...otherParam);
    String getFullHttpLiveUrlByType(String domain,String liveUrl, LiveStreamUrlType liveStreamUrlType);

    List<String> getAllShareRtmpPullUrl(String domain, String liveUrl);
    List<String> getAllShareHttpPullUrl(String domain, String liveUrl, LiveStreamUrlType liveStreamUrlType);
}
