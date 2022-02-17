package com.donglai.model.db.repository.account;

import com.donglai.model.db.entity.account.PrivateChatSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Moon
 * @date 2021-11-01 14:54
 */
public interface PrivateChatSessionRepository extends MongoRepository<PrivateChatSession, Long> {
    PrivateChatSession findByFromUidAndToUid(String fromUid, String toUid);

    List<PrivateChatSession> findByFromUidAndDelIsAndToUidInOrderByLastTimeDesc(String userId, boolean del, List<String> userIds);

    List<PrivateChatSession> findByFromUidAndToUidInAndDelIsOrderByLastTimeDesc(String fromUid, List<String> toUids, boolean del);

    List<PrivateChatSession> findByFromUidAndDelIs(String userId, boolean isDel);

    List<PrivateChatSession> findByFromUid(String userId);
}
