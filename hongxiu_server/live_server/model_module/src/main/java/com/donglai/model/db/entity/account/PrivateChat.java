package com.donglai.model.db.entity.account;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.protocol.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document
@NoArgsConstructor
public class PrivateChat {
    @Id
    @AutoIncKey
    private long id;
    @Field
    private String message;
    @Field
    private long time;
    @Indexed
    @Field
    private String fromUid;
    @Indexed
    @Field
    private String toUid;
    @Field
    private boolean read;

    private PrivateChat(String message, String fromUid, String toUid, long time) {
        this.message = message;
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.time = time;
    }

    public static PrivateChat newInstance(String message, String fromUid, String toUid, long time) {
        return new PrivateChat(message, fromUid, toUid, time);
    }

    public Account.PrivateChat toProto() {
        return Account.PrivateChat.newBuilder().setMessageId(String.valueOf(id))
                .setFromUid(fromUid).setToUid(toUid)
                .setMessage(message).setTime(String.valueOf(time))
                .setRead(read)
                .build();
    }
}
