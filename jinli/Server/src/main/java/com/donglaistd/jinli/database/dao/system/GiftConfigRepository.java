package com.donglaistd.jinli.database.dao.system;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.system.GiftConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GiftConfigRepository extends MongoRepository<GiftConfig,String> {
    List<GiftConfig> findByPlatformType(Constant.PlatformType platformType);

    void deleteByPlatformTypeIsNull();

    GiftConfig findByGiftIdAndPlatformType(String giftId, Constant.PlatformType platformType);
}
