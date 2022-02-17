package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.LiveUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LiveUserRepository extends PagingAndSortingRepository<LiveUser, String> {
    //todo add limit (cang)
    List<LiveUser> findByLiveStatus(Constant.LiveStatus status);

    Page<LiveUser> findAll(Pageable pageable);

    Optional<LiveUser> findById(String id);

    LiveUser findByRealName(String realName);

    LiveUser findByIdAndLiveStatus(String id , Constant.LiveStatus status);

    long countByUserIdIsNotNull();

    LiveUser findByUserId(String userId);

    List<LiveUser> findByPlatformType(Constant.PlatformType platformType);

    List<LiveUser> findByIdInAndPlatformTypeIs(Set<String> ids, Constant.PlatformType platformType);
}

