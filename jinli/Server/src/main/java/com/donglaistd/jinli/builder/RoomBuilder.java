package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.IdGeneratedProcess;
import com.donglaistd.jinli.util.StringNumberUtils;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

import static com.donglaistd.jinli.constant.GameConstant.ROOM_ID_LENGTH;
@Component
public class RoomBuilder {
    @Autowired
    private RoomDaoService roomDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    IdGeneratedProcess idGeneratedProcess;

    public synchronized Room create(String liveUserId,String userId, String title, String description,String roomImg){
        Room room = roomDaoService.findByLiveUserId(liveUserId);
        if(Objects.isNull(room)){
            String id = idGeneratedProcess.generatedId(Room.class.getSimpleName(), ROOM_ID_LENGTH);
            room = new Room(id, id, title, liveUserId, description, userId);
            room.setRoomImage(roomImg);
        }
        return roomDaoService.save(room);
    }

    public synchronized Room createOfficeRoom(String roomDisplayId, Constant.Pattern pattern, String liveUserId, String title,
                                              String description, String roomImg,String userId){
        if(StringUtils.isNullOrBlank(roomDisplayId))
            roomDisplayId = idGeneratedProcess.generatedId(Room.class.getSimpleName(), ROOM_ID_LENGTH);
        Room room ;
        if(roomDaoService.findByDisplayId(roomDisplayId) != null){
            room = roomDaoService.findByDisplayId(roomDisplayId);
        }
        room = new Room(roomDisplayId, roomDisplayId, title, liveUserId, description, userId);
        room.setPattern(pattern);
        room.setRoomImage(roomImg);
        dataManager.saveRoom(room);
        return room;
    }

}
