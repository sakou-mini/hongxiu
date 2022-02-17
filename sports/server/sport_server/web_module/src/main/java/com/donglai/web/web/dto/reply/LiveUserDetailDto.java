package com.donglai.web.web.dto.reply;

import com.donglai.model.db.entity.common.User;
import lombok.Data;

@Data
public class LiveUserDetailDto {
    private String userId;// id
    private String liveUserId; //主播id
    private String avatar; //头像
    private String nickname; //昵称
    private int status;//用户状态
    private long lastLoginTime;//上次登录时间
    public String device; //上次登录设备
    private String loginIp;//登录ip
    private boolean online; //是否在线

    private long monthLiveTime; //月在播时常
    private long allLiveTime; //直播总时长
    private long createTime; //注册时间
    private int liveCount; //直播次数
    public long lastLiveStartTime; //上次开播时间
    public long lastLiveTime; //上次开播时常

    public LiveUserDetailDto(User user, long monthLiveTime, long allLiveTime, int liveCount) {
        this.userId = user.getId();
        this.liveUserId = user.getLiveUserId();
        this.status = user.getStatus();
        this.nickname = user.getNickname();
        this.avatar = user.getAvatarUrl();
        this.lastLoginTime = user.getLastLoginTime();
        this.loginIp = user.getIp();
        this.online = user.isOnline();
        this.device = "未知";
        this.createTime = user.getCreateTime();
        //TODO 月在播时常
        this.monthLiveTime = monthLiveTime;
        this.allLiveTime = allLiveTime;
        this.liveCount = liveCount;

        //
    }
}
