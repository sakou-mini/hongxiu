package com.donglai.model.db.repository.common;

import com.donglai.model.db.entity.common.PersonalSetting;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonalSettingRepository extends MongoRepository<PersonalSetting, String> {
}
