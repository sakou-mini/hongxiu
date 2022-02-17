package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.RedPacketRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RedPacketRecordRepository extends MongoRepository<RedPacketRecord,ObjectId> {
    RedPacketRecord findById(String id);

    List<RedPacketRecord> findByUserId(String userId);

    RedPacketRecord findByUserIdAndRedPacketId(String userId, String redPackageId);

    List<RedPacketRecord> findByRedPacketId(String redPacketId);
}
