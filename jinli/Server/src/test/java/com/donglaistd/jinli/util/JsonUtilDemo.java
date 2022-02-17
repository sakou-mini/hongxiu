package com.donglaistd.jinli.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.RaceBuilder;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.donglaistd.jinli.constant.LiveUserConstant.DefaultQuickChat;

public class JsonUtilDemo extends BaseTest {

    @Test
    public void listToJSonTest(){
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        Gson gson = new Gson();
        String result = gson.toJson(list);
        System.out.println(result);


        String[] strings = JSONUtil.parseArray(DefaultQuickChat).toArray(new String[0]);
        System.out.println(strings);
    }

    @Test
    public void jsonListToListTest(){
        String jsonStr = "[1,2,3]";
        Gson gson = new Gson();
        List<Integer> list = gson.fromJson(jsonStr, new TypeToken<List<Integer>>(){}.getType());
        System.out.println(list);
    }

    @Test
    public void raceTest(){
        Jinli.QueryRaceListReply.Builder builder = Jinli.QueryRaceListReply.newBuilder();
        Jinli.QueryRaceListReply.Builder racesByType = RaceBuilder.getRacesByType(builder, Constant.RaceType.LANDLORDS, 2);
        System.out.println(racesByType);
        System.out.println(10_0000+1);
    }

    @Autowired
    RedisTemplate<String, LiveUser> liveUserTemplate;
    @Autowired
    RedisTemplate<String, String> stringRedisTemplate;
    @Autowired
    RedisTemplate<String, Object> objectRedisTemplate;
    @Test
    public void test(){
        User zsf = createTester(2000, "zsf");
        LiveUser liveUser = liveUserBuilder.create("asdas", Constant.LiveStatus.ONLINE, Constant.PlatformType.PLATFORM_JINLI);
        liveUserTemplate.opsForValue().set("liveUser", liveUser);
        stringRedisTemplate.opsForValue().set("text", "this is text");
        objectRedisTemplate.opsForValue().set("object1", 12312321);
    }

    @Test
    public void testUploadVip(){
        long totalNum = 0;
        for (int i = 1; i <= 60; i++) {
            totalNum  += MetaUtil.getPlayerDefineByCurrentLevel(i).getExp();
        }
        System.out.println(totalNum);
    }
}
