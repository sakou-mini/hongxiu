package com.donglai.common.util.live;

import com.donglai.common.util.HashUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.donglai.common.constant.LiveStreamConstant.*;

@Service
public class WangsuLiveStream implements LiveStream {
    public static final String AppName = "live";
    @Value("${wangsu.live.push.key}")
    private String pushKey;
    @Value("${wangsu.live.pull.key}")
    private String pullKey;

    private String generateSafeUrl(String key, String streamName, long time){
        String input= key +"/" + WangsuLiveStream.AppName + "/"+streamName + time;
        String wsSecret = HashUtils.getMd5Hash(input);
        return String.format("%s?wsSecret=%s&wsTime=%s", streamName, wsSecret, time);
    }

    @Override
    public String getRtmpPushUrl(String domain, String liveUrl, Object... otherParam) {
        String rtmpPushUrlPrefix = RTMP_PREFIX + PUSH_PREFIX + domain + LIVE_CHANNEL;
        long time = TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis());
        return rtmpPushUrlPrefix+generateSafeUrl(pushKey, liveUrl, time);
    }

    @Override
    public String getRtmpPullUrl(String domain, String liveUrl, Object... otherParam) {
        String rtmpPullUrlPrefix = RTMP_PREFIX + PULL_PREFIX + domain + LIVE_CHANNEL;
        long expireTime = TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis());
        return rtmpPullUrlPrefix+generateSafeUrl(pullKey, liveUrl, expireTime);
    }

    @Override
    public String getFullHttpLiveUrlByType(String domain, String liveUrl, LiveStreamFactory.LiveStreamUrlType liveStreamUrlType) {
        String httpUrlPrefix = HTTPS_PREFIX + PULL_PREFIX + domain + LIVE_CHANNEL;
        long expireTime = TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis());
        if (liveStreamUrlType == LiveStreamFactory.LiveStreamUrlType.m3u8) {
            String path = liveUrl + "/playlist." + liveStreamUrlType.name();
            return httpUrlPrefix + generateSafeUrl(pullKey, path, expireTime);
        }
        return httpUrlPrefix + generateSafeUrl(pullKey, liveUrl+"."+liveStreamUrlType.name(), expireTime);
    }
}
