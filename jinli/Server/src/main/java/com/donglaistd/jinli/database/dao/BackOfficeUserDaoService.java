package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class BackOfficeUserDaoService {
    @Autowired
    BackOfficeUserRepository backOfficeUserRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    MongoOperations mongoOperations;

    public PageImpl<BackOfficeUser> findAllAndPage(int page,int size){
        Criteria criteria = new Criteria();
        Pageable thePage = PageRequest.of(page,size);
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query,BackOfficeUser.class);
        PageImpl<BackOfficeUser> personData = new PageImpl<>(mongoTemplate.find(query.with(thePage), BackOfficeUser.class),PageRequest.of(page,size),count);
        return personData;
    }

    public List<BackOfficeUser> findAll(){
        return backOfficeUserRepository.findAll();
    }

    public BackOfficeUser save(BackOfficeUser backOfficeUser){
        return backOfficeUserRepository.save(backOfficeUser);
    }

    public BackOfficeUser findById(String id){
        return backOfficeUserRepository.findById(id).orElse(null);
    }

    public BackOfficeUser findByAccountName(String accountName){
        return backOfficeUserRepository.findByAccountName(accountName);
    }

    public void deleteByAccountName(String accountName) {
        backOfficeUserRepository.deleteByAccountName(accountName);
    }

    public void cleanUserRoleByAccountName(String accountName){
        UpdateResult updateResult = mongoOperations.updateFirst(new Query(Criteria.where("accountName").is(accountName)), new Update().set("role",new HashSet<>()), BackOfficeUser.class);
    }

    public void resetBackOfficeUser(List<String> accountName){
        UpdateResult updateResult = mongoOperations.updateMulti(new Query(Criteria.where("accountName").nin(accountName)), new Update().set("role",new HashSet<>()), BackOfficeUser.class);
    }
}
