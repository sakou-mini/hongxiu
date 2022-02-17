package com.donglai.model.config.mongodb;

import com.donglai.model.db.entity.common.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import static com.donglai.common.constant.CommonConstant.START_USERID;

@Configuration
@Order(value = 1)
public class MongoSetUp implements CommandLineRunner {
    @Autowired
    MongoDbSaveEventListener mongoDbSaveEventListener;

    @Override
    public void run(String... args) {
        initIncreaseId();
    }

    public void initIncreaseId() {
        System.out.println("初始化了 mongodb id");
        mongoDbSaveEventListener.initCollId(User.class.getName(), START_USERID);
    }
}
