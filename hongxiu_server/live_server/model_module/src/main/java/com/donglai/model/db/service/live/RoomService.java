package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.repository.live.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.donglai.common.constant.LiveRedisConstant.LIVING_ROOM;
import static com.donglai.common.constant.LiveRedisConstant.ROOM;


@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    RedisTemplate<String, String> redisTemplate;


    @Transactional
    @CachePut(value = ROOM, key = "#room.id", unless = "#result == null")
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Cacheable(value = ROOM, key = "#id", unless = "#result == null")
    public Room findById(String id) {
        return roomRepository.findById(id).orElse(null);
    }

    public Room findByLiveUserId(String liveUserId) {
        return roomRepository.findByLiveUserId(liveUserId);
    }

    public void addLiveRoom(String roomId) {
        redisTemplate.opsForSet().add(LIVING_ROOM, roomId);
    }

    public void removeLiveRoom(String roomId) {
        redisTemplate.opsForSet().remove(LIVING_ROOM, roomId);
    }

    public Set<String> getAllLiveRoom() {
        return redisTemplate.opsForSet().members(LIVING_ROOM);
    }

    public boolean roomIsLive(String roomId) {
        return redisTemplate.opsForSet().isMember(LIVING_ROOM, roomId);
    }

}
