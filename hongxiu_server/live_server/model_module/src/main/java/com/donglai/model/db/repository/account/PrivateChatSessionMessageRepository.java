package com.donglai.model.db.repository.account;

import com.donglai.model.db.entity.account.PrivateChatSession_Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Moon
 * @date 2021-11-01 15:23
 */
public interface PrivateChatSessionMessageRepository extends MongoRepository<PrivateChatSession_Message, Long> {
    void deleteBySessionId(long sessionId);


    List<PrivateChatSession_Message> findBySessionId(long sessionId);
}
