package com.donglai.model.config.mongodb;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.ServerPropertyService;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;

import static com.donglai.common.constant.CommonConstant.START_USERID;

@Configuration
@Order(value = 1)
public class MongoSetUp implements CommandLineRunner {
    @Resource
    MongoDbSaveEventListener mongoDbSaveEventListener;
    @Resource
    MongoTemplate mongoTemplate;
    @Override
    public void run(String... args) {
        initIncreaseId();
    }

    public void initIncreaseId() {
        System.out.println("初始化了 mongodb id");
        mongoDbSaveEventListener.initCollId(User.class.getName(), START_USERID);
    }
}
