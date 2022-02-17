package com.donglai.model.db.entity.account;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Moon
 * @date 2021-11-01 15:20
 */
@Data
@Document
@NoArgsConstructor
public class PrivateChatSession_Message {
    @Id
    @AutoIncKey
    private long id;
    @Field
    private long sessionId;
    @Field
    private long messageId;


    public PrivateChatSession_Message(long sessionId, long messageId) {
        this.sessionId = sessionId;
        this.messageId = messageId;
    }

    public static PrivateChatSession_Message newInstance(long messageId, long sendSessionId) {
        return new PrivateChatSession_Message(messageId, sendSessionId);
    }
}
