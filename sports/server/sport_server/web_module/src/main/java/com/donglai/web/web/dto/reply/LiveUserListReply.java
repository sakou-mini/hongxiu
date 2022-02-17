package com.donglai.web.web.dto.reply;

import com.donglai.model.db.entity.common.User;
import lombok.Data;

@Data
public class LiveUserListReply {
    private String userId;// id
    private String liveUserId; //主播id
    private String nickname; //昵称
    private String avatar; //头像
    private int status;//用户状态
    private long lastLoginTime;//上次登录时间
    private String loginIp;//登录ip
    private long monthOnlineTime; //月在线

    public LiveUserListReply(User user) {
        this.userId = user.getId();
        this.liveUserId = user.getLiveUserId();
        this.status = user.getStatus();
        this.nickname = user.getNickname();
        this.avatar = user.getAvatarUrl();
        this.lastLoginTime = user.getLastLoginTime();
        this.loginIp = user.getIp();
    }
}
