package com.donglai.web;

import com.donglai.web.app.WebApp;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApp.class)
@ActiveProfiles("test")
public abstract class WebBaseTest {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        clearAllData();
    }

    @After
    public void tearDown() {
        clearAllData();
    }

    protected void clearAllData() {
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("*")));
        for (var collectionName : mongoTemplate.getCollectionNames()) {
            var collection = mongoTemplate.getCollection(collectionName);
            collection.deleteMany(new Document());
        }
    }
}
