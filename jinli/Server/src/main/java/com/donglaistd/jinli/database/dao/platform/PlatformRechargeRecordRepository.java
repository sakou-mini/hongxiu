package com.donglaistd.jinli.database.dao.platform;

import com.donglaistd.jinli.database.entity.plant.PlatformRechargeRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlatformRechargeRecordRepository extends MongoRepository<PlatformRechargeRecord, String> {
}
