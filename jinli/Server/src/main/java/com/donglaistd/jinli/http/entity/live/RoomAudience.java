package com.donglaistd.jinli.http.entity.live;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.User;

public class RoomAudience {
    public String userId;
    public String displayName;
    public String avatar;
    public Constant.VipType vipType;
    public long gameCoin;
    public boolean isMute;
    public AudienceIdentity audienceIdentity;
    public String platformId;

    public RoomAudience(User user, boolean isMute, AudienceIdentity audienceIdentity) {
        this.userId = user.getId();
        this.displayName = user.getDisplayName();
        this.avatar = user.getAvatarUrl();
        this.vipType = user.getVipType();
        this.gameCoin = user.getGameCoin();
        this.isMute = isMute;
        this.audienceIdentity = audienceIdentity;
        this.platformId = user.getPlatformShowUserId();
    }

    public enum AudienceIdentity{
        AUDIENCE(0),ADMINISTRATOR(1),LIVEUSER(2);
        int code;
        AudienceIdentity(int code) {
            this.code = code;
        }
        public AudienceIdentity valueOf(int typeValue){
            switch (typeValue){
                case 0:
                    return AUDIENCE;
                case 1:
                    return ADMINISTRATOR;
                case 2:
                    return LIVEUSER;
            }
            return null;
        }
    }
}
