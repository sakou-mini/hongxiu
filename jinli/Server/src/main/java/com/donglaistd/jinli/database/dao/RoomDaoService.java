package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class RoomDaoService{
    @Autowired
    private RoomRepository roomRepository;

    @Transactional
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    public void deleteAll() {
        roomRepository.deleteAll();
    }

    public Room findByLiveUser(LiveUser liveUser){
       return roomRepository.findByLiveUserId(liveUser.getId());
    }

    public Room findByLiveUserId(String liveUserId){
        return roomRepository.findByLiveUserId(liveUserId);
    }

    public long count(){
        return roomRepository.count();
    }

    public List<Room> findByRoomTitle(String title){
        return roomRepository.findByRoomTitle(title);
    }

    public Room findByDisplayId(String displayId) {return roomRepository.findByDisplayId(displayId);}

    public boolean containRoomDisplayId(String roomDisplayId){
        return roomRepository.existsByDisplayId(roomDisplayId);
    }

    public void deleteRoom(Room room){
        roomRepository.delete(room);
    }

    public boolean isNotUsedRoomTitle(String title){
        List<Room> rooms = findByRoomTitle(title);
        for (Room room : rooms) {
            if( Objects.nonNull(DataManager.findOnlineRoom(room.getId())))
                return false;
        }
        return true;
    }
}
