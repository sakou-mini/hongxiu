package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.live.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoomRepository extends MongoRepository<Room, String> {
	Room findByLiveUserId(String liveUserId);
}
