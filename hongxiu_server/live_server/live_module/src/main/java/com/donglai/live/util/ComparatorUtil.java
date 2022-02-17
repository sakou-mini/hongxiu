package com.donglai.live.util;

import com.donglai.live.process.MessageListProcess;
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

    public static Comparator<MessageListProcess.MessageUserList> getMessageUserListComparatorByIsLive() {
        return (user1, user2) -> Boolean.compare(user2.isLive(), user1.isLive());
    }
}
