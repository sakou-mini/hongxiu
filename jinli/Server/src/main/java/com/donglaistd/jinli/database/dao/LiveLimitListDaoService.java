package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.LiveLimitList;
import com.donglaistd.jinli.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LiveLimitListDaoService {
    @Autowired
    LiveLimitListRepository liveLimitListRepository;

    public LiveLimitList save(LiveLimitList liveLimitList) {
        return liveLimitListRepository.save(liveLimitList);
    }

    public List<LiveLimitList> saveAll(List<LiveLimitList> liveLimitLists) {
        return liveLimitListRepository.saveAll(liveLimitLists);
    }

    public LiveLimitList findByPlatformAndLimitDate(Constant.PlatformType platform, long date){
        return liveLimitListRepository.findByPlatformAndLimitDateIs(platform, TimeUtil.getTimeDayStartTime(date));
    }

    public void deleteByPlatformAndLimitDate(Constant.PlatformType platform, long date){
        liveLimitListRepository.deleteByPlatformAndLimitDateIs(platform, TimeUtil.getTimeDayStartTime(date));
    }

    public List<LiveLimitList> findLiveUserLimitList(String liveUserId, Constant.PlatformType platform){
        List<LiveLimitList> liveLimitLists = liveLimitListRepository.findAllByPlatformAndLimitDateGreaterThanEqualOrderByLimitDateAsc(platform, TimeUtil.getCurrentDayStartTime());
        List<LiveLimitList> liveUserLimitLists = new ArrayList<>();
        for (LiveLimitList liveLimitList : liveLimitLists) {
            if(liveLimitList.containsLiveUser(liveUserId))
                liveUserLimitLists.add(liveLimitList);
        }
        return liveUserLimitLists;
    }
}
