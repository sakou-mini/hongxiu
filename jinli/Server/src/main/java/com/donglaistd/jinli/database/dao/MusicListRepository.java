package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.Music;
import com.donglaistd.jinli.database.entity.MusicList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MusicListRepository extends MongoRepository<MusicList,String> {
    MusicList findByUserId(String userId);
}
