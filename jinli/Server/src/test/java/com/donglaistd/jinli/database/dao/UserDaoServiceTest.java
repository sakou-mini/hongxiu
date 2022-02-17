package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.UserAttribute;
import com.donglaistd.jinli.http.dto.request.UserListRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class UserDaoServiceTest extends BaseTest {
    @Autowired
    UserDaoService userDaoService;

    @Test
    public void IncreaseUserGameCoinTest() {
        var result = userDaoService.increaseGameCoin(user.getId(), 100);
        Assert.assertEquals(1, result.getModifiedCount());
        user = userDaoService.findById(user.getId());
        Assert.assertEquals(100, user.getGameCoin());

        result = userDaoService.increaseGameCoin(user.getId(), -50);
        Assert.assertEquals(1, result.getModifiedCount());
        user = userDaoService.findById(user.getId());
        Assert.assertEquals(50, user.getGameCoin());
    }

    @Autowired
    UserAttributeDaoService userAttributeDaoService;

    @Test
    public void getUserListByConditionTest(){
        User user1 =  userBuilder.createUser("zsf", "张三丰", "", "", false);
        user1.setPhoneNumber("123456");
        userDaoService.save(user1);
        UserAttribute userAttribute = UserAttribute.newInstance(user1.getId(), Constant.AccountStatue.ACCOUNT_NORMAL);
        userAttributeDaoService.save(userAttribute);
        User user2 = createTester(1000, "lisi");
        UserAttribute userAttribute2 = UserAttribute.newInstance(user2.getId(), Constant.AccountStatue.ACCOUNT_BAN);
        userAttributeDaoService.save(userAttribute2);
        PageInfo<User> users = userDaoService.getUserListByCondition("", "","", Constant.AccountStatue.ACCOUNT_NORMAL,1,3, Constant.PlatformType.PLATFORM_JINLI);
        Assert.assertEquals(1,users.getContent().size());
    }

    @Test
    public void testGroupByPlantType(){
        User user1 = userBuilder.createUser("1", "1", "1");
        user1.setPlatformType(Constant.PlatformType.PLATFORM_JINLI);
        dataManager.saveUser(user1);
        User user2 = userBuilder.createUser("2", "2", "2");
        user2.setPlatformType(Constant.PlatformType.PLATFORM_T);
        dataManager.saveUser(user2);
        Map<Constant.PlatformType, List<String>> platformTypeUser = userDaoService.groupUserInfoByPlatformType();
        Assert.assertEquals(2,platformTypeUser.size());
        Assert.assertEquals(1,platformTypeUser.get(Constant.PlatformType.PLATFORM_T).size());
        Assert.assertEquals(2,platformTypeUser.get(Constant.PlatformType.PLATFORM_JINLI).size());
    }

    @Test
    public void test(){
        UserAttribute attribute = userAttributeDaoService.findByUserIdOrSaveIfNotExit(user.getId());
        attribute.addIpHistory("192.168.0.113");
        userAttributeDaoService.save(attribute);
        user.setLastLoginTime(System.currentTimeMillis());
        userDaoService.save(user);


        UserListRequest userListRequest = new UserListRequest();
        userListRequest.setPlatform(Constant.PlatformType.PLATFORM_JINLI_VALUE);
        userListRequest.setIp("192.168.0.113");
        PageInfo<User> byUserListRequest = userDaoService.findByUserListRequest(userListRequest);
    }
}
