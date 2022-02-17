package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.live.GiftLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftLogRepository extends MongoRepository<GiftLog, String> {
    List<GiftLog> findBySenderId(String senderId);

    List<GiftLog> findByReceiveId(String receiverId);
}