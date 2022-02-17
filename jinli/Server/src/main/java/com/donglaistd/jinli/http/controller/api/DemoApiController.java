package com.donglaistd.jinli.http.controller.api;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.LiveStreamUrlType;
import com.donglaistd.jinli.database.dao.system.LiveDomainConfigDaoService;
import com.donglaistd.jinli.database.entity.system.LiveDomainConfig;
import com.donglaistd.jinli.util.live.LiveStream;
import com.donglaistd.jinli.util.live.LiveStreamFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/demo")
public class DemoApiController {
    @Autowired
    LiveDomainConfigDaoService liveDomainConfigDaoService;
    private static final Logger logger = Logger.getLogger(DemoApiController.class.getName());

    @RequestMapping(value = "/getLivePullUrl")
    public String getDemoLiveStreamURl() {
        String streamUrl = "demo";
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(Constant.LiveSourceLine.WANGSU_LINE);
        LiveDomainConfig liveDomainConfig = liveDomainConfigDaoService.findByLine(Constant.LiveSourceLine.WANGSU_LINE);
        String liveDomain = liveDomainConfig.getDomains().isEmpty() ? "" : liveDomainConfig.getDomains().get(0);
        String rtmpPushUrl = liveStream.getRtmpPushUrl(liveDomain,streamUrl);
        logger.info("demo push url is:"+rtmpPushUrl);
        return liveStream.getFullHttpLiveUrlByType(liveDomain, streamUrl, LiveStreamUrlType.flv);
    }

    @RequestMapping(value = "/getLiveUrl")
    public Map<String, String>  getDemoLiveURl(String liveUrl) {
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(Constant.LiveSourceLine.WANGSU_LINE);
        LiveDomainConfig liveDomainConfig = liveDomainConfigDaoService.findByLine(Constant.LiveSourceLine.WANGSU_LINE);
        String liveDomain = liveDomainConfig.getDomains().isEmpty() ? "" : liveDomainConfig.getDomains().get(0);
        String pushUrl = liveStream.getRtmpPushUrl(liveDomain, liveUrl);
        String flv = liveStream.getFullHttpLiveUrlByType(liveDomain, liveUrl, LiveStreamUrlType.flv);
        String hls = liveStream.getFullHttpLiveUrlByType(liveDomain, liveUrl, LiveStreamUrlType.m3u8);
        Map<String, String> streamMap =  new HashMap<>();
        streamMap.put("pushUrl",pushUrl);
        streamMap.put("pullFlv",flv);
        streamMap.put("pullHls",hls);
        return streamMap;
    }
}
