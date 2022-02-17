package com.donglai.live.util;

import com.donglai.live.BaseTest;
import com.donglai.protocol.Constant;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MapTest extends BaseTest {

    @Test
    public void test(){
        Map<Constant.PlatformType, Map<String, String>> platformUserMap = new HashMap<>();
        platformUserMap.computeIfAbsent(Constant.PlatformType.SPORT, k -> new HashMap<>()).put("1", "1");
        platformUserMap.computeIfAbsent(Constant.PlatformType.SPORT, k -> new HashMap<>()).put("2", "2");
        platformUserMap.computeIfAbsent(Constant.PlatformType.SPORT, k -> new HashMap<>()).put("333", "333");
        platformUserMap.computeIfAbsent(Constant.PlatformType.SPORT, k -> new HashMap<>()).put("3334", "3334");
    }
}
