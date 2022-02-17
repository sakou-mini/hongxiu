package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static com.donglaistd.jinli.Constant.LiveStatus.UNUPLOAD_IMAGE;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class UserOperationService {
    @Autowired
    DataManager dataManager;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    VerifyUtil verifyUtil;


    public Constant.UserType getUserType(User user){
        Constant.UserType value;
        LiveUser liveUser = liveUserDaoService.findByUserId(user.getId());
        if (Objects.nonNull(liveUser)) {
            if ((liveUser.getLiveStatus().equals(Constant.LiveStatus.UNAPPROVED) || liveUser.getLiveStatus().equals(Constant.LiveStatus.APPROVED_FAIL))) {
                value = Constant.UserType.LIVE_UNAPPROVED;
            } else if(Objects.equals(UNUPLOAD_IMAGE,liveUser.getLiveStatus())) {
                value = Constant.UserType.LIVE_UNAPPROVED;
            }else
                value = Constant.UserType.LIVE;
        } else {
            value = Constant.UserType.NORMAL;
        }
        return value;
    }

    public void updateUserExp(User user,long exp){
        user.updateLevel(exp);
        boolean result = user.updateVipByLevel();
        if(result){
            User onlineUser = dataManager.findOnlineUser(user.getId());
            if (onlineUser!=null && Objects.nonNull(onlineUser.getCurrentRoomId())) {
                Optional.ofNullable(DataManager.findOnlineRoom(onlineUser.getCurrentRoomId())).ifPresent(r -> {
                    Jinli.UpgradeBroadcastMessage.Builder builder = Jinli.UpgradeBroadcastMessage.newBuilder();
                    Jinli.UserInfo.Builder userInfo = Jinli.UserInfo.newBuilder();
                    userInfo.setUserId(user.getId())
                            .setPhoneNumber(Objects.isNull(user.getPhoneNumber()) ? "未绑定" : user.getPhoneNumber())
                            .setLevel(user.getLevel()).setAvatarUrl(user.getAvatarUrl())
                            .setDisplayName(user.getDisplayName())
                            .setUserType(getUserType(user))
                            .setGameCoin(user.getGameCoin())
                            .setVipId(user.getVipType())
                            .build();
                    r.broadCastByUser(buildReply(builder.setUserInfo(userInfo)),onlineUser);
                });
            }
        }
    }
}
