package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.builder.QPlatformGameConfigBuilder;
import com.donglaistd.jinli.config.QPlatformGameConfig;
import com.donglaistd.jinli.service.api.PlatformQApiService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class OrderIdUtilsTest extends BaseTest {

    @Test
    public void orderIdTest() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 100000; i++) {
            boolean add = ids.add(OrderIdUtils.getOrderNumber());
        }
        System.out.println(ids);
        Assert.assertEquals(100000, ids.size());
    }
}
