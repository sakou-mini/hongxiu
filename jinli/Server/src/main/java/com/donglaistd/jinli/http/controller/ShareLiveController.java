package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.builder.UserBuilder;
import com.donglaistd.jinli.constant.LiveStreamUrlType;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.dao.statistic.DailyDownloadRecordDaoService;
import com.donglaistd.jinli.database.dao.system.GiftConfigDaoService;
import com.donglaistd.jinli.database.dao.system.LiveDomainConfigDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.system.LiveDomainConfig;
import com.donglaistd.jinli.service.ServerAvailabilityCheckService;
import com.donglaistd.jinli.service.UploadServerService;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.live.LiveStream;
import com.donglaistd.jinli.util.live.LiveStreamFactory;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.logging.Logger;

@Controller
@RequestMapping("/share")
public class ShareLiveController {
    Logger logger = Logger.getLogger(ShareLiveController.class.getName());
    @Value("${server.version}")
    public String version;

    @Autowired
    ServerAvailabilityCheckService serverAvailabilityCheckService;
    @Autowired
    DataManager dataManager;
    @Autowired
    UserDaoService userDaoService;
    @Autowired()
    UserBuilder userBuilder;
    @Autowired
    UploadServerService uploadServerService;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    DailyDownloadRecordDaoService dailyDownloadRecordDaoService;
    @Autowired
    GiftConfigDaoService giftConfigDaoService;
    @Autowired
    LiveDomainConfigDaoService liveDomainConfigDaoService;


    @RequestMapping("/index")
    @ResponseBody
    public Object handleRequest(HttpServletRequest request, HttpServletResponse response, String userId) {
        User user = userDaoService.findById(userId);
        LiveUser liveUser = liveUserDaoService.findById(user.getLiveUserId());
        Room room = roomDaoService.findByLiveUser(liveUser);
        ModelAndView modelAndView = new ModelAndView("video/index.html");
        modelAndView.addObject("displayName", user.getDisplayName());
        modelAndView.addObject("avatarUrl", user.getAvatarUrl());
        modelAndView.addObject("roomTitle", room.getRoomTitle());
        return  modelAndView;

    }

    @RequestMapping("/hlsVideo")
    @ResponseBody
    public Object hlsVideo(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("video/hlsVideo.html");
    }

    @RequestMapping("/flvVideo")
    @ResponseBody
    public Object flvVideo(HttpServletRequest request, HttpServletResponse response,String liveUrl) {
        return new ModelAndView("video/flvVideo.html");
    }

    @RequestMapping("/test1")
    @ResponseBody
    public Object test1(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("video/test1.html");
    }

    @RequestMapping("/playerTest")
    @ResponseBody
    public Object playerTest(){
        return new ModelAndView("video/playerTest.html");
    }

    @RequestMapping("/getLiveUrl")
    @ResponseBody
    public Object getLiveUrl(HttpServletRequest request, HttpServletResponse response, String userId){
        logger.info("receieve GetLiveUrl Request!   userId is---->"+userId);
        User user = userDaoService.findById(userId);
        if(StringUtils.isNullOrBlank(user.getLiveUserId())) return "offline";
        LiveUser liveUser = dataManager.findLiveUser(user.getLiveUserId());
        if(liveUser.getLiveStatus() == Constant.LiveStatus.OFFLINE){
            return  "offline";
        }
        else {
            return getShareLiveUrl(liveUser);
        }
    }

    @RequestMapping("/shareApplication")
    @ResponseBody
    public ModelAndView shareUserApplication(@RequestParam String inviteCode) {
        ModelAndView modelAndView = new ModelAndView("video/download.html");
        modelAndView.addObject("inviteCode", inviteCode);
        return modelAndView;
    }

    @RequestMapping("/downloadApp")
    @ResponseBody
    public Object downloadApp() {
        dailyDownloadRecordDaoService.addDownloadCountByToday();
        return "downLoadAddress";
    }

    @RequestMapping("/getLiveUserStatus")
    @ResponseBody
    public Object getLiveUserStatus(String userId){
        User user = userDaoService.findById(userId);
        LiveUser liveUser = dataManager.findLiveUser(user.getLiveUserId());
        if(liveUser.getLiveStatus() == Constant.LiveStatus.OFFLINE){
            return "OFFLINE";
        }
        else{
            return "ONLINE";
        }
    }

    private Map<String, List<String>> getShareLiveUrl(LiveUser liveUser){
        Map<String, List<String>> liveUrlMap = new HashMap<>();
        Room room = DataManager.findOnlineRoom(liveUser.getRoomId());
        Constant.LiveSourceLine line = room.getLiveSourceLine();
        String liveDomain = room.getLiveDomain();
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(line);
        String liveUrlM3u8 = LiveStreamFactory.getLiveStreamByLiveSourceLine(line).getFullHttpLiveUrlByType(liveDomain, liveUser.getLiveUrl(), LiveStreamUrlType.m3u8);
        liveUrlMap.put(LiveStreamUrlType.flv.name(),  liveStream.getAllShareHttpPullUrl(liveDomain, liveUser.getLiveUrl(), LiveStreamUrlType.flv));
        liveUrlMap.put(LiveStreamUrlType.m3u8.name(), Lists.newArrayList(liveUrlM3u8));
        logger.info("livePullurl is:  "+liveUrlMap);
        return liveUrlMap;
    }

    @RequestMapping("/giftJson")
    @ResponseBody
    public Object getGiftJson(Constant.PlatformType platform){
        return giftConfigDaoService.getFullGiftConfigForJSon(platform).toMap();
    }

    @RequestMapping("/getLiveDomain")
    @ResponseBody
    public Object getLiveDomain(Constant.LiveSourceLine line){
        LiveDomainConfig liveDomainConfig = liveDomainConfigDaoService.findByLine(line);
        return liveDomainConfig == null ? LiveDomainConfig.newInstance(line, new ArrayList<>()) : liveDomainConfig;
    }

    @RequestMapping("/resetLiveDomain")
    @ResponseBody
    public Object getLiveDomain(){
        liveDomainConfigDaoService.deleteAllDomainConfig();
        liveDomainConfigDaoService.initDomainConfig();
        return "success";
    }

    @RequestMapping("/setLiveDomain")
    @ResponseBody
    public Object setLiveDomain(Constant.LiveSourceLine line,String liveDomain){
        LiveDomainConfig byLine = liveDomainConfigDaoService.findByLine(line);
        byLine.setDomains(Lists.newArrayList(liveDomain));
        liveDomainConfigDaoService.save(byLine);
        return "success";
    }

    @GetMapping("/statusCheck")
    @ResponseBody
    public Map<String,Object> serverCheck(){
        Map<String, Object> result = new HashMap<>();
        result.put("version", version);
        result.put("statue", serverAvailabilityCheckService.isActive() ? "active": "disabled");
        return result;
    }
}
