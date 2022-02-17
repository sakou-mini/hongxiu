package com.donglaistd.jinli.database.dao;


import com.donglaistd.jinli.database.entity.Blacklist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlacklistDaoService {
    @Autowired
    private BlacklistRepository blacklistRepository;
    public Blacklist save(Blacklist blacklist) {
        return blacklistRepository.save(blacklist);
    }
    public void deleteById(String id) {
        blacklistRepository.deleteById(id);
    }

    public Blacklist findByRoomId(String roomId) {
        return blacklistRepository.findByRoomId(roomId);
    }
}
