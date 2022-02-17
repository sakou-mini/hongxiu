package com.donglai.model.db.service.common;

import com.donglai.model.db.entity.common.PersonalSetting;
import com.donglai.model.db.repository.common.PersonalSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.donglai.common.constant.RedisConstant.USER_SETTING;

@Service
public class PersonalSettingService {
    @Autowired
    PersonalSettingRepository repository;

    @CachePut(value = USER_SETTING, key = "#result.userId", unless = "#result == null")
    public PersonalSetting save(PersonalSetting personalSetting) {
        return repository.save(personalSetting);
    }

    @Cacheable(value = USER_SETTING, key = "#userId", unless = "#result == null")
    public PersonalSetting findByUserId(String userId) {
        return repository.findById(userId).orElse(PersonalSetting.newInstance(userId));
    }
}
