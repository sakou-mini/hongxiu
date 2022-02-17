package com.donglaistd.jinli.util.live;

import com.donglaistd.jinli.constant.LiveStreamUrlType;
import com.donglaistd.jinli.util.Utils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.donglaistd.jinli.constant.LiveConstant.*;

@Service
public class WangsuLiveStream implements LiveStream{
    public static final String AppName = "live";
    @Value("${wangsu.live.push.key}")
    private String pushKey;
    @Value("${wangsu.live.pull.key}")
    private String pullKey;

    @Override
    public String getRtmpPushUrl(String domain, String liveUrl, Object... otherParam) {
        String prefix = RTMP_PREFIX + PUSH_PREFIX + domain + LIVE_CHANNEL;
        long time = TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis());
        return prefix + generateSafeUrl(pushKey, AppName,liveUrl, time);
    }

    @Override
    public String getRtmpPullUrl(String domain, String liveUrl, Object... otherParam) {
        String prefix = RTMP_PREFIX + PULL_PREFIX + domain + LIVE_CHANNEL;
        long expireTime = TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis());
        return prefix + generateSafeUrl(pullKey, AppName,liveUrl, expireTime);
    }

    @Override
    public String getFullHttpLiveUrlByType(String domain, String liveUrl, LiveStreamUrlType liveStreamUrlType) {
        String prefix = HTTPS_PREFIX + PULL_PREFIX + domain + LIVE_CHANNEL;
        long expireTime = TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis());
        if (liveStreamUrlType == LiveStreamUrlType.m3u8) {
            String path = liveUrl + "/playlist." + liveStreamUrlType.name();
            return prefix + generateSafeUrl(pullKey, AppName, path, expireTime);
        }
        return prefix + generateSafeUrl(pullKey, AppName,liveUrl+"."+liveStreamUrlType.name(), expireTime);
    }

    @Override
    public List<String> getAllShareRtmpPullUrl(String domain, String liveUrl) {
        return Lists.newArrayList(getRtmpPullUrl(domain, liveUrl));
    }

    @Override
    public List<String> getAllShareHttpPullUrl(String domain, String liveUrl, LiveStreamUrlType liveStreamUrlType) {
        return Lists.newArrayList(getFullHttpLiveUrlByType(domain, liveUrl,liveStreamUrlType));
    }

    private String generateSafeUrl(String key, String appName,String streamName, long time){
        String input= key +"/" + appName + "/"+streamName + time;
        String wsSecret = Utils.getMd5Hash(input);
        return String.format("%s?wsSecret=%s&wsTime=%s", streamName, wsSecret, time);
        //https://pull.bfszhl.com/live/6e6e5a46584e4dcbb65763f791fe9257.flv?wsSecret=f1e0d29dd52774d42b5811609f680109&wsTime=1633949018
        //https://pull.bfszhl.com/live/6e6e5a46584e4dcbb65763f791fe9257.flv?wsSecret=3e1dc88fb45302a333e7d799d47e55f6&wsTime=1633949354
    }
}
