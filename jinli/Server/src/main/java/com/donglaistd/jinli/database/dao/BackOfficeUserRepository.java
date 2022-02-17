package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface BackOfficeUserRepository extends MongoRepository<BackOfficeUser, String> {
    BackOfficeUser findByAccountName(String accountName);

    void deleteByAccountName(String accoutnName);
}
