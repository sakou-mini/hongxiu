package com.donglaistd.jinli.database.entity.component;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;

public class UserSummaryInfo {
    private String userId;
    private String nickName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private UserSummaryInfo(String userId, String nickName) {
        this.userId = userId;
        this.nickName = nickName;
    }

    public static UserSummaryInfo newInstance(User user){
        return new UserSummaryInfo(user.getId(), user.getDisplayName());
    }
    public static UserSummaryInfo newInstance(String userId, String nickName){
        return new UserSummaryInfo(userId,nickName);
    }

    public Jinli.ReplyUserInfo toReplyUserInfo(){
        return Jinli.ReplyUserInfo.newBuilder().setUserId(userId).setUserName(nickName).build();
    }
}
