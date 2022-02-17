package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.http.entity.RtmpOriginInfo;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;

@Component
public class RtmpOriginService {
    private Logger logger = Logger.getLogger(RtmpOriginService.class.getName());

    @Autowired
    UserDaoService userDaoService;
    @Autowired
    DataManager dataManager;
    public boolean verifyPush(RtmpOriginInfo originInfo){
        if (StringUtils.isNullOrBlank(originInfo.getUserId()) || StringUtils.isNullOrBlank(originInfo.getToken()) || StringUtils.isNullOrBlank(originInfo.getChannel())) {
            logger.warning("rtmp auth missing origin param:" + originInfo);
            return false;
        }
        User user = userDaoService.findById(originInfo.getUserId());
        if(Objects.isNull(user) || StringUtils.isNullOrBlank(user.getLiveUserId()))
        {
            logger.warning("rtmp auth  user not accord live:" + user);
            return false;
        }
        LiveUser liveUser = dataManager.findLiveUser(user.getLiveUserId());
        if (Objects.isNull(liveUser) || !Objects.equals(Constant.LiveStatus.ONLINE, liveUser.getLiveStatus()))
        {
            logger.warning("rtmp auth  liveUser not live:"+liveUser);
            return false;
        }
        if(!liveUser.getRoomId().equals(originInfo.getRoomId())){
            logger.warning("rtmp auth room not equals:");
            return false;
        }
        if(!liveUser.getLiveUrl().equals(originInfo.getChannel())){
            logger.warning("rtmp auth liveUrl not equals:");
            return false;
        }
        if (!Objects.equals(liveUser.getRtmpCode(), originInfo.getToken())) {
            logger.warning("rtmp auth rtmpCode not equals:");
            return false;
        }
        return true;
    }

    public boolean verifyPull(RtmpOriginInfo originInfo){
        if (StringUtils.isNullOrBlank(originInfo.getUserId()) || StringUtils.isNullOrBlank(originInfo.getRoomId())
                || StringUtils.isNullOrBlank(originInfo.getChannel()) || StringUtils.isNullOrBlank(originInfo.getIp())) {
            logger.warning("rtmp auth missing origin param:" + originInfo);
            return false;
        }
        User user = userDaoService.findById(originInfo.getUserId());
        if(Objects.isNull(user))
        {
            logger.warning("rtmp auth  user exit:"+user);
            return false;
        }
        String userIp = dataManager.getUserRemoteAddress(user.getId());
        if(!Objects.equals(userIp,originInfo.getIp()) && !user.isPlatformUser()){
            logger.warning("rtmp auth  user ip not matching:"+ userIp);
            return false;
        }
        Room onlineRoom = DataManager.findOnlineRoom(originInfo.getRoomId());
        if(Objects.isNull(onlineRoom)){
            logger.warning("rtmp auth  room not exit");
            return false;
        }
        if(onlineRoom.notContainsUser(user) && !user.isPlatformUser()){
            logger.warning("user not in room");
            return false;
        }
        LiveUser liveUser = dataManager.findLiveUser(onlineRoom.getLiveUserId());
        if(Objects.isNull(liveUser ) || !liveUser.getLiveUrl().equals(originInfo.getChannel())) {
            logger.warning("rtmp auth liveUser not live or liveUrl not equals");
            return false;
        }
        return true;
    }

}
