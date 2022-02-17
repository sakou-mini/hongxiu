package com.donglaistd.jinli.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RandomUtilTest {

    @Test
    public void randomCoinListTest() {
        int coin = 20000;
        int num = 10;
        int minCoin = 3;
        List<Integer> randomCoinList = RandomUtil.getRandomCoinList(coin, num, minCoin, 2000l);
        System.out.println("随机的列表为：" + randomCoinList);
        int sum = randomCoinList.stream().mapToInt(Integer::intValue).sum();
        Assert.assertEquals(coin, sum);
        Integer maxValue = randomCoinList.stream().max(Integer::compareTo).get();
        Assert.assertEquals(9502, maxValue, 0);
        Integer minValue = randomCoinList.stream().min(Integer::compareTo).get();
        Assert.assertEquals(2, minValue, 0);
    }

    @Test
    public void randomRoomDisplayIdTest(){
        Set<String> sttr = new HashSet<>();
        for (int i = 0; i <10000 ; i++) {
            String s = StringUtils.generateRoomDisplayId(i);
            sttr.add(s);
        }
        Assert.assertEquals(10000,sttr.size());
    }
}