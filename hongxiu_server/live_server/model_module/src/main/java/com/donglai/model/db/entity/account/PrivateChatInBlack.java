package com.donglai.model.db.entity.account;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Moon
 * @date 2021-11-01 17:55
 */
@Data
@Document
@NoArgsConstructor
public class PrivateChatInBlack {
    @Id
    @AutoIncKey
    private long id;
    @Indexed
    @Field
    private String userId;
    @Indexed
    @Field
    private String blackUserId;
    @Field
    private boolean del;

    public PrivateChatInBlack(String userId, String blackUserId, boolean del) {
        this.userId = userId;
        this.blackUserId = blackUserId;
        this.del = del;
    }

    public static PrivateChatInBlack newInstance(String userId, String blackUserId) {
        return new PrivateChatInBlack(userId, blackUserId, false);
    }
}
