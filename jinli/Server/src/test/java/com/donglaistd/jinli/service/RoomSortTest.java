package com.donglaistd.jinli.service;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.util.ComparatorUtil;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

public class RoomSortTest extends BaseTest {

    @Test
    public void roomRecommendSortedTest(){
        Constant.PlatformType platform = Constant.PlatformType.PLATFORM_JINLI;
        Room room1 = new Room();
        room1.setPlatformRecommendWeight(platform, 1);

        Room room2 = new Room();
        room2.setPlatformRecommendWeight(platform,3);

        Room room3 = new Room();
        room3.setPlatformRecommendWeight(platform,2);

        Room room4 = new Room();
        room4.setHot(false);

        Room room5 = new Room();
        room5.setHot(true);

        Room room6 = new Room();

        Room room7 = new Room();

        ArrayList<Room> rooms = Lists.newArrayList(room1, room2, room6, room3, room4, room5,room7);
        Collections.shuffle(rooms);
        rooms.sort(ComparatorUtil.getRoomRecommendComparator(platform));
        Assert.assertEquals(3,rooms.get(0).getPlatformRecommendWeight(platform));
    }

}
