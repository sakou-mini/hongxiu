package com.donglaistd.jinli.util.live;

import com.donglaistd.jinli.constant.LiveStreamUrlType;
import com.donglaistd.jinli.database.entity.LiveUser;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.donglaistd.jinli.constant.LiveConstant.*;
import static com.donglaistd.jinli.constant.LiveConstant.PULL_PREFIX;

@Service
public class NginxLiveStream implements LiveStream{

    @Override
    public String getRtmpPushUrl(String domain, String liveUrl, Object... otherParam) {
        if(otherParam.length <= 0) return "";
        if(!(otherParam[0] instanceof LiveUser)) return "";
        LiveUser liveUser = (LiveUser) otherParam[0];
        String prefix = RTMP_PREFIX + PUSH_PREFIX + domain;
        return prefix +liveUrl+"?userId="+liveUser.getUserId()+"&roomId="+liveUser.getRoomId()+"&code="+liveUser.getRtmpCode();
    }

    @Override
    public String getRtmpPullUrl(String domain,String liveUrl, Object... otherParam) {
        return liveUrl;
    }

    @Override
    public String getFullHttpLiveUrlByType(String domain,String liveUrl, LiveStreamUrlType liveStreamUrlType) {
        String prefix = HTTPS_PREFIX + PULL_PREFIX + domain;
        return prefix + liveUrl+"."+LiveStreamUrlType.flv.name();
    }

    @Override
    public List<String> getAllShareRtmpPullUrl(String domain, String liveUrl) {
        return null;
    }

    @Override
    public List<String> getAllShareHttpPullUrl(String domain, String liveUrl, LiveStreamUrlType liveStreamUrlType) {
        return null;
    }
}
