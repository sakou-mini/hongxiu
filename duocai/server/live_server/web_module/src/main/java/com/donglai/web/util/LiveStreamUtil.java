package com.donglai.web.util;

import com.donglai.common.util.live.LiveStream;
import com.donglai.common.util.live.LiveStreamFactory;
import com.donglai.model.db.entity.live.Room;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
@Slf4j
public class LiveStreamUtil {

    public static Map<String, Object> getPullUrl(Room room){
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(room.getLiveLineCode());
        assert liveStream != null;
        String m3u8Url = liveStream.getFullHttpLiveUrlByType(room.getLiveDomain(), room.getLiveCode(), LiveStreamFactory.LiveStreamUrlType.m3u8);
        String flvUrl = liveStream.getFullHttpLiveUrlByType(room.getLiveDomain(), room.getLiveCode(), LiveStreamFactory.LiveStreamUrlType.flv);
        String rtmpPushUrl = liveStream.getRtmpPushUrl(room.getLiveDomain(), room.getLiveCode());
        Map<String, Object> liveUrlMap = new HashMap<>();
        liveUrlMap.put(LiveStreamFactory.LiveStreamUrlType.m3u8.name(), m3u8Url);
        liveUrlMap.put(LiveStreamFactory.LiveStreamUrlType.flv.name(), flvUrl);
        liveUrlMap.put("livePattern", room.getPattern().getNumber());
        log.info("推流地址为：{}", liveStream.getRtmpPushUrl(room.getLiveDomain(), room.getLiveCode()));
        return liveUrlMap;
    }

}
