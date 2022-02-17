package com.donglai.live.util;

import com.donglai.model.db.entity.live.Room;

import java.util.Comparator;

public class ComparatorUtil {

    public static Comparator<Room> getRoomAudienceComparator() {
        return (room1, room2) -> {
            int sortNum1 = room1.getAudiences().size();
            int sortNum2 = room2.getAudiences().size();
            return sortNum2 - sortNum1;
        };
    }
}
