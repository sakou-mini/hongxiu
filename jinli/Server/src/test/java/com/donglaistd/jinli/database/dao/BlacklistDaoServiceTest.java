package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.entity.Blacklist;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlacklistDaoServiceTest extends BaseTest {
    @Autowired
    private BlacklistDaoService blackListDaoService;

    @Test
    public void saveBlacklistTest() {
        Blacklist instance = Blacklist.getInstance(room.getId(), user.getId());
        blackListDaoService.save(instance);
        Blacklist byRoomId = blackListDaoService.findByRoomId(room.getId());
        Assert.assertNotNull(byRoomId);
    }

}
