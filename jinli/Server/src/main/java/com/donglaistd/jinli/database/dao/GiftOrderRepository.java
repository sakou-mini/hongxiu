package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.GiftOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GiftOrderRepository extends MongoRepository<GiftOrder,String> {
    List<GiftOrder> findBySenderId(String senderId);
}
