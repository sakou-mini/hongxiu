package com.donglai.model.db.entity.account;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.protocol.Account;
import com.donglai.protocol.Common;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Moon
 * @date 2021-11-01 14:55
 */
@Data
@Document
@NoArgsConstructor
public class PrivateChatSession {
    @Id
    @AutoIncKey
    private long id;
    @Field
    private String lastMessage;
    @Field
    private long lastTime;
    @Indexed
    @Field
    private String fromUid;
    @Indexed
    @Field
    private String toUid;
    @Field
    private boolean del;

    public PrivateChatSession(String lastMessage, long lastTime, String fromUid, String toUid, boolean isDel) {
        this.lastMessage = lastMessage;
        this.lastTime = lastTime;
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.del = isDel;
    }

    public static PrivateChatSession newInstance(String lastMessage, long lastTime,
                                                 String fromUid, String toUid, boolean isDel) {
        return new PrivateChatSession(lastMessage, lastTime, fromUid, toUid, isDel);
    }

    public Account.PrivateChatSession toProto(long unread, Common.UserInfo info) {
        return Account.PrivateChatSession.newBuilder()
                .setId(String.valueOf(this.id))
                .setLastTime(String.valueOf(this.lastTime))
                .setLastMessage(this.lastMessage)
                .setFromUid(this.fromUid)
                .setToUid(this.toUid)
                .setUnread(String.valueOf(unread))
                .setUserInfo(info)
                .build();
    }
}
