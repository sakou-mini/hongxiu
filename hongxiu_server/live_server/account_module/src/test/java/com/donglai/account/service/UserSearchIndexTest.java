package com.donglai.account.service;

import com.donglai.account.BaseTest;
import com.donglai.account.entityBuilder.UserBuilder;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.service.impl.es.UserElasticsearchServiceImpl;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSearchIndexTest extends BaseTest {
    @Autowired
    UserElasticsearchServiceImpl userElasticsearchServiceImpl;
    @Autowired
    UserService userService;

    @Autowired
    UserBuilder userBuilder;

    @Test
    public void test() throws IOException, ParseException, InterruptedException {
        userElasticsearchServiceImpl.deleteAll();
        User user1 = userBuilder.createTourist("code", "pwd");
        user1.setNickname("张三丰");
        userService.save(user1);
        User user2 = userBuilder.createTourist("code", "pwd");
        user2.setNickname("liming,当然我也叫张三");
        user2.setSignatureText("我是小明的朋友");
        userService.save(user2);

        User user3 = userBuilder.createTourist("code", "pwd");
        user3.setNickname("小明");
        userService.save(user3);
        userElasticsearchServiceImpl.synCreatIndex();
        Map<String, String> param = new HashMap<>();
        param.put("nickname", "张三");
        List<User> userList = userElasticsearchServiceImpl.searchFuzzyQuery(param);
        System.out.println(userList);
        Thread.sleep(1000L);
        Assert.assertEquals(2, userList.size());

        //更新索引
        user3.setNickname("张三的徒弟");
        userService.save(user3);
        Thread.sleep(1000L);
        param.put("nickname", "张三");
        userList = userElasticsearchServiceImpl.searchFuzzyQuery(param);
        Assert.assertEquals(3, userList.size());
    }

    @After
    public void after(){
        userElasticsearchServiceImpl.deleteAll();
    }
}
