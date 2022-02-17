package com.donglai.blogs.test;

import com.donglai.blogs.app.BlogsApp;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.common.PersonalSetting;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.PersonalSettingService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.ProtoBufMapper;
import com.donglai.protocol.message.KafkaMessage;
import com.donglai.protocol.util.PbRefUtil;
import com.google.protobuf.Message;
import org.assertj.core.util.Lists;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.donglai.blogs.constant.Constant.VIDEO_MP4_SUFFIX;
import static com.donglai.common.constant.PathConstant.DEFAULT_AVATAR_BASE_PATH;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BlogsApp.class)
@ActiveProfiles("test")
public abstract class BaseTest {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    UserService userService;
    @Autowired
    BlogsService blogsService;
    @Autowired
    PersonalSettingService personalSettingService;

    protected User user;

    @Before
    public void initBlogsData() {
        user = createDefaultUser();
        List<Blogs> allPassBlogs = blogsService.findAllPassBlogs();
        if (allPassBlogs.size() < 2) {
            List<Blogs> blogs = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Blogs thumbnail = Blogs.newInstance(user.getId(), "官方模拟的动态" + i, Constant.BlogsStatus.BLOGS_PASS, Constant.BlogsType.BLOGS_VIDEO, Lists.newArrayList("thumbnail"));
                thumbnail.setResourceUrl(com.google.common.collect.Lists.newArrayList(DEFAULT_AVATAR_BASE_PATH + i + VIDEO_MP4_SUFFIX));
                blogs.add(thumbnail);
            }
            blogsService.saveAll(blogs);
        }
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

    public User createDefaultUser() {
        User user = new User("accountId", "nickName", "password", "mobileCode");
        user.setId("10252");
        user.setTourist(false);
        user = userService.save(user);
        PersonalSetting personalSetting = PersonalSetting.newInstance(user.getId());
        personalSettingService.save(personalSetting);
        return user;
    }

    public User createUser(String id, String accountId) {
        User user = new User(accountId, "nickName", "password", "mobileCode");
        user.setId(id);
        user.setTourist(false);
        user = userService.save(user);
        PersonalSetting personalSetting = PersonalSetting.newInstance(id);
        personalSettingService.save(personalSetting);
        return user;
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

    @SuppressWarnings("unchecked")
    public static <T> T parseMessage(KafkaMessage.TopicMessage topicMessage) {
        try {
            String messageName = PbRefUtil.getPbRefSimpleNameByMessageId(ProtoBufMapper.MessageType.REPLY_MSG, topicMessage.getMessageId());
            Message message = (Message) PbRefUtil.getPbRefObj(ProtoBufMapper.MessageType.REPLY_MSG, topicMessage.getMessageId(), topicMessage.getContent());
            return (T) message;
        } catch (Exception e) {
            return null;
        }
    }
}
