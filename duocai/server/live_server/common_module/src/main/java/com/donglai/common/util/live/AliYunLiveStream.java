package com.donglai.common.util.live;

import com.donglai.common.util.HashUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.donglai.common.constant.LiveStreamConstant.*;

@Service
public class AliYunLiveStream implements LiveStream {
    public static final String AppName = "live";

    @Value("${aliyun.live.push.key}")
    private String pushKey;

    @Value("${aliyun.live.pull.key}")
    private String pullKey;

    @Value("${tencent.live.expired.time}")
    private long liveUrlExpireTime;


    private static String getMd5HashString(String path,long expireTime,String rand,int uid,String key){
        String src = String.format("%s-%s-%s-%s-%s", path, expireTime, rand, uid, key);
        return HashUtils.getMd5Hash(src);
    }

    @Override
    public String getRtmpPushUrl(String domain,String liveUrl,Object ...otherParam) {
        String prefix = RTMP_PREFIX + PUSH_PREFIX + domain + LIVE_CHANNEL;
        return  prefix + generateSafeUrl(AppName,liveUrl, liveUrlExpireTime, pushKey);
    }

    @Override
    public String getRtmpPullUrl(String domain,String liveUrl,Object ...otherParam) {
        String prefix = RTMP_PREFIX + PULL_PREFIX + domain + LIVE_CHANNEL;
        return prefix + generateSafeUrl(AppName,liveUrl, liveUrlExpireTime, pullKey);
    }

    private static String generateSafeUrl(String appName, String streamName, long txTime, String key){
        String path = "/" + appName+"/"+streamName;
        long expireTime = TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() + txTime);
        String rand = "0"; //Utils.createUUID();
        int uid = 0;
        String hashValue = getMd5HashString(path, expireTime, rand, uid, key);
        String authKey = String.format("%s-%s-%s-%s", expireTime, rand, uid, hashValue);
        return String.format("%s?auth_key=%s", streamName, authKey);
    }

    @Override
    public String getFullHttpLiveUrlByType(String domain,String liveUrl, LiveStreamFactory.LiveStreamUrlType liveStreamUrlType) {
        String prefix = HTTPS_PREFIX + PULL_PREFIX + domain + LIVE_CHANNEL;
        return prefix + generateSafeUrl(AppName, liveUrl+ "."+liveStreamUrlType.name(), liveUrlExpireTime, pullKey);
    }
}
