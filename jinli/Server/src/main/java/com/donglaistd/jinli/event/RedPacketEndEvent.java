package com.donglaistd.jinli.event;

import com.donglaistd.jinli.database.entity.RedPacket;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;

import java.util.ArrayList;
import java.util.List;

public class RedPacketEndEvent implements BaseEvent {
    private List<RedPacket> redPackets = new ArrayList<>();
    private final Room room;
    private boolean cleanAll = false;

    public List<RedPacket> getRedPacketList() {
        return redPackets;
    }

    public Room getRoom() {
        return room;
    }

    public boolean isCleanAll() {
        return cleanAll;
    }

    public RedPacketEndEvent(RedPacket redPacket, Room room) {
        redPackets.add(redPacket);
        this.room = room;
    }

    public RedPacketEndEvent(List<RedPacket> redPacketList, Room room, boolean cleanAll) {
        this.redPackets = redPacketList;
        this.room = room;
        this.cleanAll = true;
    }
}
