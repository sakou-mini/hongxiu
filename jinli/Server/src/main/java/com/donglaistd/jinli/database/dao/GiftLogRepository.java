package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftLogRepository extends MongoRepository<GiftLog,String> {
    List<GiftLog> findBySenderId(String senderId);
    List<GiftLog> findByReceiveId(String receiverId);
    List<GiftLog> findByPlatformType(Constant.PlatformType platformType);
    long countBySenderId(String userId);
}