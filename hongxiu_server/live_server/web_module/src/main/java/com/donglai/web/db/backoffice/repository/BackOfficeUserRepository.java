package com.donglai.web.db.backoffice.repository;

import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BackOfficeUserRepository extends MongoRepository<BackOfficeUser, String> {
    BackOfficeUser findByUsername(String username);

    List<BackOfficeUser> findByIdIn(List<String> ids);

    List<BackOfficeUser> deleteByIdIn(List<String> ids);
}
