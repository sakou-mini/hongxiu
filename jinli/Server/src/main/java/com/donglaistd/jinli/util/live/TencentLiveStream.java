package com.donglaistd.jinli.util.live;

import com.donglaistd.jinli.constant.LiveStreamUrlType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.donglaistd.jinli.constant.LiveConstant.*;

@Component
public class TencentLiveStream implements LiveStream{
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    @Value("${tencent.live.expired.time}")
    private long liveUrlExpireTime;
    @Value("${tencent.live.push.key}")
    private String pushKey;
    @Value("${tencent.live.pull.key}")
    private String pullKey;
    @Value("${tencent.live.nodes}")
    public List<String> domains = new ArrayList<>();

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
        String prefix = RTMP_PREFIX + PUSH_PREFIX + domain + LIVE_CHANNEL;
        long expireTime = TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() + liveUrlExpireTime);
        return prefix + liveUrl+generateSafeUrl(pushKey, liveUrl, expireTime);
    }

    @Override
    public String getRtmpPullUrl(String domain, String liveUrl, Object... otherParam) {
        String prefix = RTMP_PREFIX + PULL_PREFIX + domain + LIVE_CHANNEL;
        long expireTime = TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() + liveUrlExpireTime);
        return prefix + liveUrl+generateSafeUrl(pullKey, liveUrl, expireTime);
    }

    @Override
    public String getFullHttpLiveUrlByType(String domain, String liveUrl, LiveStreamUrlType liveStreamUrlType) {
        String prefix = HTTPS_PREFIX + PULL_PREFIX + domain + LIVE_CHANNEL;
        long expireTime = TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() + liveUrlExpireTime);
        return String.format("%s%s.%s%s",prefix, liveUrl, liveStreamUrlType.name(),generateSafeUrl(pullKey, liveUrl, expireTime));
    }

    @Override
    public List<String> getAllShareRtmpPullUrl(String domain, String liveUrl) {
        List<String> pullUrl = new ArrayList<>();
        if(!domains.contains(domain))  pullUrl.add(getRtmpPullUrl(domain, liveUrl));
        for (String tempDomain : domains) {
            pullUrl.add(getRtmpPullUrl(tempDomain, liveUrl));
        }
        return pullUrl;
    }

    @Override
    public List<String> getAllShareHttpPullUrl(String domain, String liveUrl, LiveStreamUrlType liveStreamUrlType) {
        List<String> pullUrl = new ArrayList<>();
        if(!domains.contains(domain))  pullUrl.add(getFullHttpLiveUrlByType(domain, liveUrl,liveStreamUrlType));
        for (String tempDomain : domains) {
            pullUrl.add(getFullHttpLiveUrlByType(tempDomain, liveUrl,liveStreamUrlType));
        }
        return pullUrl;
    }
}
