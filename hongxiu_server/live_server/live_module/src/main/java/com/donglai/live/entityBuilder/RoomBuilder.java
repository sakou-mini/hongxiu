package com.donglai.live.entityBuilder;

import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.model.util.IdGeneratedProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglai.live.constant.Constant.DEFAULT_ROOM_IMAGE;

@Component
public class RoomBuilder {
    @Value("${roomId.length}")
    private int idLength;
    @Autowired
    RoomService roomService;
    @Autowired
    IdGeneratedProcess idGeneratedProcess;

    public Room createRoom(String liveUserId, String userId) {
        Room room = roomService.findByLiveUserId(liveUserId);
        if (Objects.isNull(room)) {
            room = new Room(idGeneratedProcess.generatedId(Room.class.getSimpleName(), idLength), liveUserId, userId);
            room.setRoomImage(DEFAULT_ROOM_IMAGE);
        }
        room.setRoomTitle("");
        return roomService.save(room);
    }
}