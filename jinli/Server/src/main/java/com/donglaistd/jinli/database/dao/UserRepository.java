package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface UserRepository extends MongoRepository<User, String> {
    User findByAccountName(String accountName);

    Page<User> findAll(Pageable pageable);

    User findByLiveUserId(String id);

    User findByDisplayName(String displayName);

    User findByPhoneNumber(String phoneNumber);

    User findByIdAndPhoneNumber(String id,String phoneNumber);

    @Query(collation = "{'locale':'en', 'strength':2}")
    boolean existsByAccountName(String accountName);

    boolean existsByDisplayName(String displayName);

    boolean existsByDisplayNameAndAccountNameIsNot(String displayName,String accountName);

    boolean existsByPhoneNumber(String phoneNumber);

    List<User> findByIdIn(List<String> userIds);

    List<User> findAllByScriptUserIs(boolean scriptUser);

    int countByPlatformType(Constant.PlatformType platformType);

    int countByPlatformTypeAndLastLoginTimeGreaterThan(Constant.PlatformType platformType,long loginTime);

    int countByPlatformTypeAndLastLoginTimeGreaterThanAndLiveUserIdNotIn(Constant.PlatformType platformType,long loginTime,List<String> liveUserId);

    int countByLastLoginTimeGreaterThanAndLiveUserIdNotIn(long loginTime,List<String> liveUserId);

    User findByPlatformUserId(String id);
}
