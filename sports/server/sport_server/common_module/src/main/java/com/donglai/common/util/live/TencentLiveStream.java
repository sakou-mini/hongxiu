package com.donglai.common.util.live;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import static com.donglai.common.constant.LiveStreamConstant.*;

@Component
public class TencentLiveStream implements LiveStream {
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    @Value("${tencent.live.expired.time}")
    private long liveUrlExpireTime;
    @Value("${tencent.live.push.key}")
    private String pushKey;
    @Value("${tencent.live.pull.key}")
    private String pullKey;

    private static String generateSafeUrl(String key, String streamName, long txTime) {
        String input = key + streamName + Long.toHexString(txTime).toUpperCase();
        String txSecret = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            txSecret  = getMd5HashString(messageDigest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String authKey = String.format("?txSecret=%s&txTime=%s",txSecret, Long.toHexString(txTime).toUpperCase());
        return txSecret == null ? "" : authKey;
    }

    private static String getMd5HashString(byte[] data) {
        char[] out = new char[data.length << 1];
        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }

    @Override
    public String getRtmpPushUrl(String domain, String liveUrl, Object... otherParam) {
        String rtmpUrlPrefix = RTMP_PREFIX + PUSH_PREFIX + domain + LIVE_CHANNEL;
        long expireTime = TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() + liveUrlExpireTime);
        return rtmpUrlPrefix +liveUrl+generateSafeUrl(pushKey, liveUrl, expireTime);
    }

    @Override
    public String getRtmpPullUrl(String domain, String liveUrl, Object... otherParam) {
        String rtmpUrlPrefix = RTMP_PREFIX + PULL_PREFIX + domain + LIVE_CHANNEL;
        long expireTime = TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() + liveUrlExpireTime);
        return rtmpUrlPrefix+liveUrl+generateSafeUrl(pullKey, liveUrl, expireTime);
    }

    @Override
    public String getFullHttpLiveUrlByType(String domain, String liveUrl, LiveStreamFactory.LiveStreamUrlType liveStreamUrlType) {
        String httpUrlPrefix = HTTPS_PREFIX + PULL_PREFIX + domain + LIVE_CHANNEL;
        long expireTime = TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() + liveUrlExpireTime);
        return String.format("%s%s.%s%s", httpUrlPrefix,liveUrl, liveStreamUrlType.name(),generateSafeUrl(pullKey, liveUrl, expireTime));
    }
}
