package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.CoinFlow;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class LiveUserDetail {
    public String liveUserId;
    public int fansNum;
    public long totalGiftIncome;
    public long totalGameFlow;
    public String phoneNumber;
    public Constant.PlatformType platform;
    public Constant.LiveStatus status;
    public long monthLiveTime;
    public long totalLiveTime;
    public long liveCount;
    public UserSummary userSummary;
    public LiveSummary latestLiveSummary;
    public long totalAudienceNum;
    public String roomDisplayId;
    private Set<Constant.LiveUserPermission> disablePermissions;
    public Set<Constant.PlatformType> sharedPlatform;
    public long auditTime;
    public LiveUserDetail(String liveUserId, int fansNum, long totalGiftIncome, String phoneNumber,
                          Constant.PlatformType platform, Constant.LiveStatus status, long monthLiveTime, long totalLiveTime, long liveCount, long totalGameFlow,String roomDisplayId) {
        this.liveUserId = liveUserId;
        this.fansNum = fansNum;
        this.totalGiftIncome = totalGiftIncome;
        this.phoneNumber = phoneNumber;
        this.platform = platform;
        this.status = status;
        this.monthLiveTime = monthLiveTime;
        this.totalLiveTime = totalLiveTime;
        this.liveCount = liveCount;
        this.totalGameFlow = totalGameFlow;
        this.roomDisplayId = roomDisplayId;
    }

    public Set<Constant.LiveUserPermission> getDisablePermissions() {
        return disablePermissions;
    }

    public void setDisablePermissions(Set<Constant.LiveUserPermission> disablePermissions) {
        this.disablePermissions = disablePermissions;
    }

    public static class UserSummary {
        public String userId;
        public int level;
        public Constant.VipType vipType;
        public long gameCoin;
        public long totalFlow;
        public long lastLoginTime;
        public String lastMobileModel;
        public Constant.MobilePhoneBrand lastBrand;
        public String lastIp;
        public long createDate;
        public boolean online;
        public String avatarUrl;
        public String inviteCode;
        public String disPlayName;
        public UserSummary(String userId, int level, Constant.VipType vipType, long gameCoin, long totalFlow,
                           long lastLoginTime, String lastMobileModel, String lastIp, long createDate, boolean online, String avatarUrl,String disPlayName) {
            this.userId = userId;
            this.level = level;
            this.vipType = vipType;
            this.gameCoin = gameCoin;
            this.totalFlow = totalFlow;
            this.lastLoginTime = lastLoginTime;
            this.lastMobileModel = lastMobileModel;
            this.lastIp = lastIp;
            this.createDate = createDate;
            this.online = online;
            this.avatarUrl = avatarUrl;
            this.disPlayName = disPlayName;
        }

        public UserSummary(User user, CoinFlow coinFlow, Constant.MobilePhoneBrand brand,String inviteCode) {
            this.userId = user.getId();
            this.level = user.getLevel();
            this.vipType = user.getVipType();
            this.gameCoin = user.getGameCoin();
            this.totalFlow = coinFlow.getFlow();
            this.lastLoginTime = user.getLastLoginTime();
            this.lastMobileModel = user.getLastMobileModel();
            this.lastBrand = brand;
            this.lastIp = user.getLastIp();
            this.createDate = user.getCreateDate().getTime();
            this.online =  Objects.nonNull(DataManager.getUserChannel(user.getId()));
            this.avatarUrl = user.getAvatarUrl();
            this.disPlayName = user.getDisplayName();
            this.inviteCode = inviteCode;
        }
    }

    public static class LiveSummary {
        public String roomId;
        public String roomTitle;
        public long liveStartTime;
        public int audienceNum;
        public String roomImage;
        public Constant.GameType gameType;

        public long latestLiveStartTime;
        public long latestLiveTime;
        public long latestGiftIncome;

        public LiveSummary() {
        }

        public LiveSummary(long latestLiveStartTime, long latestLiveTime,long latestGiftIncome) {
            this.latestLiveStartTime = latestLiveStartTime;
            this.latestLiveTime = latestLiveTime;
            this.latestGiftIncome = latestGiftIncome;
        }
    }
}
