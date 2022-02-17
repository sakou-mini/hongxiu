package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.system.GiftConfigDaoService;
import com.donglaistd.jinli.database.entity.system.GiftConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class GiftConfigDaoTest extends BaseTest {
    @Autowired
    GiftConfigDaoService giftConfigDaoService;

    @Test
    public void giftDataInitTest() throws IOException {
        giftConfigDaoService.initGiftConfig();
        var file = new File("config/json/gift.json");
        Assert.assertTrue(file.exists());
        JSONObject jsonObject = new JSONObject(new JSONTokener(new FileInputStream(file)));
        var configs = new ObjectMapper().readValue(jsonObject.getJSONArray("giftLists").toString(), GiftConfig[].class);
        Assert.assertEquals(12, configs.length);
        List<GiftConfig> allGift = giftConfigDaoService.findAllByPlatform(Constant.PlatformType.PLATFORM_JINLI);
        Assert.assertEquals(12, allGift.size());
    }
}
