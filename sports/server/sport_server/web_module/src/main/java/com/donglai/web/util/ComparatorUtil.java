package com.donglai.web.util;

import com.donglai.model.db.entity.live.Room;
import com.donglai.web.db.backoffice.entity.Menu;

import java.util.Comparator;

public class ComparatorUtil {

    public static Comparator<Menu> getParentMenuComparator(){
        return (menu1, menu2) -> {
            int sortNum1 = Integer.parseInt(menu1.getId());
            int sortNum2 =  Integer.parseInt(menu2.getId());
            return sortNum1 - sortNum2;
        };
    }

    public static Comparator<Room> getRoomComparator(){
        return (room1, room2) -> (int) (room1.getLiveStartTime() - room2.getLiveStartTime());
    }

}
