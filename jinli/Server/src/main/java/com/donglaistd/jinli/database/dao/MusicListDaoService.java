package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.MusicList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MusicListDaoService {
    @Autowired
    MusicListRepository musicListRepository;

    public MusicList save(MusicList musicList){
        return musicListRepository.save(musicList);
    }

    public MusicList findByUserId(String userId){
        return musicListRepository.findByUserId(userId);
    }


}
