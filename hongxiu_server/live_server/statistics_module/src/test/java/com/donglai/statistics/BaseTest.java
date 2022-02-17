package com.donglai.statistics;

import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.ProtoBufMapper;
import com.donglai.protocol.message.KafkaMessage;
import com.donglai.protocol.util.PbRefUtil;
import com.donglai.statistics.app.StatisticsApp;
import com.google.protobuf.Message;
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

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StatisticsApp.class)
@ActiveProfiles("test")
public abstract class BaseTest {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    UserService userService;

    @Before
    public void setUp() {

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

    public HongXiu.HongXiuMessageRequest buildMessageRequest(Message message) {
        HongXiu.HongXiuMessageRequest.Builder request = HongXiu.HongXiuMessageRequest.newBuilder();
        try {
            String name = "set" + message.getDescriptorForType().getName().replaceAll("_", "");
            var f = request.getClass().getMethod(name, message.getClass());
            f.invoke(request, message);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return request.build();
    }

    public static Message getReplyMessage(KafkaMessage.TopicMessage topicMessage) {
        return (Message) PbRefUtil.getPbRefObj(ProtoBufMapper.MessageType.REPLY_MSG, topicMessage.getMessageId(), topicMessage.getContent());
    }
}
