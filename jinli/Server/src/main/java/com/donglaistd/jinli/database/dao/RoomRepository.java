package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.Room;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RoomRepository extends MongoRepository<Room, String> {
	Room findById(ObjectId id);

	Room findByLiveUserId(String liveUserId);

	List<Room> findByRoomTitle(String roomTitle);

	Room findByDisplayId(String displayId);

	boolean existsByDisplayId(String roomDisplayId);

}
