package com.donglai.model.config.mongodb;

import com.donglai.model.db.entity.blogs.BlogsLabelsConfig;
import com.donglai.model.db.entity.common.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

import static com.donglai.common.constant.SystemConstant.START_LABELS_ID;
import static com.donglai.common.constant.SystemConstant.START_USERID;

@Configuration
@Order(value = 1)
@Slf4j
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
        mongoDbSaveEventListener.initCollId(BlogsLabelsConfig.class.getName(), START_LABELS_ID);
    }


    @Bean
    @ConditionalOnProperty(name="spring.data.mongodb.transactionEnabled",havingValue = "true")
    MongoTransactionManager transactionManager(MongoDatabaseFactory factory){
        log.info("开启了mongodb 事务");
        return new MongoTransactionManager(factory);
    }
}
