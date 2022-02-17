package com.donglai.account.app;

import com.donglai.account.process.QueueProcess;
import com.donglai.common.service.RedisService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(value = 1)
public class SetUp implements CommandLineRunner {
    @Autowired
    RedisService redisService;
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        // queueProcess.initDailyTask();
        //cleanRedis();
    }

    public void cleanRedis(){
        List<String> keys = new ArrayList<>(redisService.keys("*"));
        redisService.del(keys);
    }

    public void cleanDb(){
        for (var collectionName : mongoTemplate.getCollectionNames()) {
            var collection = mongoTemplate.getCollection(collectionName);
            collection.deleteMany(new Document());
        }
    }
}
