package com.donglai.live.thread;

import com.donglai.live.BaseTest;
import com.donglai.live.entityBuilder.UserBuilder;
import com.donglai.model.config.mongodb.MongoDbSaveEventListener;
import com.donglai.model.db.service.account.PrivateChatService;
import com.donglai.model.db.service.common.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

@Slf4j
public class ThreadTest extends BaseTest {

    @Autowired
    UserBuilder userBuilder;
    @Autowired
    UserService userService;
    @Autowired
    PrivateChatService privateChatService;
    @Autowired
    MongoDbSaveEventListener mongoDbSaveEventListener;
    @Autowired
    MongoTemplate mongo;

    @Test
    public void multiThread() throws InterruptedException {
        //Query query = new Query(Criteria.where("collName").is(User.class.getName()));
        //NumberIdInfo one = mongo.findOne(query, NumberIdInfo.class);
        //System.out.println(one);
        //ExecutorService executorService = MyThreadPool.getbgThread();
        //for (int i = 0; i <10000 ; i++) {
        //    executorService.submit(()->{
        //        User user = userBuilder.createUser("", "mobileCode");
        //        user = userService.save(user);
        //        log.info("create user{},for thread{}",user,Thread.currentThread().getName());
        //    });
        //}
        //executorService.shutdown();
        //while (true){
        //    if(executorService.isTerminated()){
        //        long count = userService.count();
        //        Assert.assertEquals(10000,count);
        //        break;
        //    }
        //}
    }
}
