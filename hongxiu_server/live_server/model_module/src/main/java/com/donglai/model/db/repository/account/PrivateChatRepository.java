package com.donglai.model.db.repository.account;

import com.donglai.model.db.entity.account.PrivateChat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PrivateChatRepository extends MongoRepository<PrivateChat, Long> {


    List<PrivateChat> findByToUidAndReadIs(String toUid, boolean param);

    /**
     * 根据ID集合查询所有聊天数据
     *
     * @param ids 聊天记录ID
     * @return 返回数据集
     */
    List<PrivateChat> findByIdInOrderByTimeAsc(List<Long> ids);

    /**
     * @param messageIds
     * @param userId
     * @return
     */
    List<PrivateChat> findByIdInAndToUidIs(List<Long> messageIds, String userId);

    long countByToUidAndReadIs(String toUid, boolean b);
}
