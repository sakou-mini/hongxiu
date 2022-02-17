package com.donglaistd.jinli.database.dao.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.system.LiveSourceLineConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LiveSourceLineConfigDaoService {
    @Autowired
    LiveSourceLineConfigRepository liveSourceLineConfigRepository;

    public LiveSourceLineConfig findLiveSourceLineConfigByPlatformType(Constant.PlatformType platformType) {
        LiveSourceLineConfig defaultConfig = new LiveSourceLineConfig(platformType);
        return liveSourceLineConfigRepository.findById(String.valueOf(platformType.getNumber())).orElse(defaultConfig);
    }

    public void initLiveSourceLineConfig(){
        liveSourceLineConfigRepository.deleteAll();
        LiveSourceLineConfig liveSourceLineConfig = new LiveSourceLineConfig(Constant.PlatformType.PLATFORM_JINLI);
        save(liveSourceLineConfig);
    }

    public LiveSourceLineConfig save(LiveSourceLineConfig liveSourceLineConfig){
        return liveSourceLineConfigRepository.save(liveSourceLineConfig);
    }
}
