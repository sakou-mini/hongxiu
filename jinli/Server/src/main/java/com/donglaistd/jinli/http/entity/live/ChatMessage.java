package com.donglaistd.jinli.http.entity.live;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.chat.MessageRecord;

public class ChatMessage {
    public String userId;
    public String displayName;
    public Constant.VipType vipType;
    public long time;
    public String message;

    public ChatMessage() {
    }

    public ChatMessage(MessageRecord messageRecord, User user) {
        this.userId = user.getPlatformShowUserId();
        this.displayName = user.getDisplayName();
        this.vipType = user.getVipType();
        this.time = messageRecord.getSendTime();
        this.message = messageRecord.getMsg();
    }
}
