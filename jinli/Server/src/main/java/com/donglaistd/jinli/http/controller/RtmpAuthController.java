package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.http.entity.RtmpOriginInfo;
import com.donglaistd.jinli.service.RtmpOriginService;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

@Controller
@RequestMapping("/rtmp")
public class RtmpAuthController {
    private static final Logger logger = Logger.getLogger(RtmpAuthController.class.getName());

    @Autowired
    UserDaoService userDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    RtmpOriginService rtmpOriginService;

    @RequestMapping("/auth")
    @ResponseBody
    public Object auth(HttpServletRequest request, HttpServletResponse response, String userId, String code, String name) {
        if(StringUtils.isNullOrBlank(userId) || StringUtils.isNullOrBlank(code) || StringUtils.isNullOrBlank(name)) {
            return authError(response);
        }
        User user = userDaoService.findById(userId);
        if (!verifyRequest(user, code, name)) {
            logger.info("user  " + user + " code = " + code + " name = " + name);
            return authError(response);
        }

        dataManager.findLiveUser(user.getLiveUserId()).setRtmpLive(true);
        return "success";
    }

    @RequestMapping("/done")
    @ResponseBody
    public Object done(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> parameter = request.getParameterMap();
        if (!parameter.containsKey("userId") || !parameter.containsKey("code") || !parameter.containsKey("name")) {
            return "error";
        }

        String userId = parameter.get("userId")[0];
        String code = parameter.get("code")[0];
        String name = parameter.get("name")[0];

        User user = userDaoService.findById(userId);

        if (!verifyRequest(user, code, name)) return authError(response);

        dataManager.findLiveUser(user.getLiveUserId()).setRtmpLive(false);
        return "success";

    }


    @RequestMapping("/authAudio")
    @ResponseBody
    public Object auth_audio(HttpServletRequest request, HttpServletResponse response, String userId, String roomId, String code) {
        if (Objects.isNull(userId)) {
            logger.info("userId is null !");
            return authError(response);
        }
        User user = dataManager.findOnlineUser(userId);
        if (Objects.isNull(roomId)) {
            logger.warning("roomId is null !");
            return authError(response);
        }
        Room room = DataManager.findOnlineRoom(roomId);
        if (Objects.isNull(room) || !room.getCurrentConnectLiveUserId().equals(user.getId())) {
            logger.info(" room is null or userId not equals ");
            return authError(response);
        }
        if (!verifyAudio(user, room, code)) {
            return authError(response);
        }
        return "success";
    }

    @RequestMapping("/doneAudio")
    @ResponseBody
    public Object done_audio(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> parameter = request.getParameterMap();
        if (!parameter.containsKey("userId") || !parameter.containsKey("roomId") || !parameter.containsKey("name") || !parameter.containsKey("code")) {
            return "error";
        }
        String userId = parameter.get("userId")[0];
        String roomId = parameter.get("roomId")[0];
        String code = parameter.get("code")[0];

        logger.warning("this is doneAudio + userId: " + userId + " ,roomId:" + roomId + " ,code: " + code);
        User user = dataManager.findOnlineUser(userId);
        Room room = DataManager.findOnlineRoom(roomId);
        if (!verifyAudio(user, room, code)) {
            return authError(response);
        }
        return "success";
    }

    private boolean verifyRequest(User user, String code, String url) {
        if (Objects.isNull(user)) {
            logger.info("user is null");
            return false;
        }
        var liveUser = dataManager.findLiveUser(user.getLiveUserId());

        if (liveUser == null) {
            logger.info("liveUser is null");
            return false;
        }
        if (liveUser.getRoomId() == null) {
            logger.info("roomId is null");
            return false;
        }
        String liveUrl = liveUser.getLiveUrl();
        if (liveUrl == null) {
            logger.info("liveUrl is null");
            return false;
        }
        if (!liveUrl.equals(url)) {
            logger.info("liveUrl not equals " + liveUrl + " != " + url);
            return false;
        }
        if (!code.equals(liveUser.getRtmpCode())) {
            logger.info("code not equals " + code + " != " + liveUser.getRtmpCode());
            return false;
        }
        return true;
    }


    private Object authError(HttpServletResponse response) {
        response.setHeader("error", "error");
        response.setStatus(500);
        return "error";
    }

    private boolean verifyAudio(User user, Room room, String code) {
        if (Objects.isNull(user)) {
            logger.info("user is null !");
            return false;
        }
        if (!user.isOnline()) {
            logger.info("user is not online !");
            return false;
        }
        if (Objects.isNull(room)) {
            logger.info("room is null !");
            return false;
        }
        if (!room.getAllPlatformAudienceList().contains(user.getId())) {
            logger.info("user has left the room !");
            return false;
        }
        if (!room.hasConnectLive(user.getId())) {
            logger.info("The user is not in the microphone list !");
            return false;
        }
        String userCode = room.getConnectLiveCodeByUserId(user.getId()).getRight();
        if (!Objects.equals(code, userCode)) {
            logger.info("code not equals : " + code + " != " + userCode);
            return false;
        }
        return true;
    }

    @RequestMapping("/pushCheck")
    @ResponseBody
    public Object pushCheck(HttpServletRequest request, HttpServletResponse response,RtmpOriginInfo originInfo){
        request.getParameterMap().forEach((key, value) -> logger.info("k " + key + " v = " + String.join(",", value)));
        logger.info("CND pushCheck:" + originInfo.toString());
        if(!rtmpOriginService.verifyPush(originInfo)){
            return 0;
        }
        logger.info("push check SUCCESS");
        return 1;
    }

    @RequestMapping("/pullCheck")
    @ResponseBody
    public Object pullCheck(HttpServletRequest request, HttpServletResponse response,RtmpOriginInfo originInfo){
        request.getParameterMap().forEach((key, value) -> logger.info("k " + key + " v = " + String.join(",", value)));
        if(!rtmpOriginService.verifyPull(originInfo)){
            return 0;
        }
        logger.info("pull check SUCCESS");
        return 1;
    }

}
