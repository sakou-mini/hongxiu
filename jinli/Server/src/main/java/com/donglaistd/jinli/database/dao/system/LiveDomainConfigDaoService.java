package com.donglaistd.jinli.database.dao.system;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.config.LiveDomainDefaultConfig;
import com.donglaistd.jinli.database.entity.system.LiveDomainConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class LiveDomainConfigDaoService {
    private final Logger logger = Logger.getLogger(LiveDomainConfigDaoService.class.getName());
    @Autowired
    public LiveDomainConfigRepository liveDomainConfigRepository;

    @Autowired
    LiveDomainDefaultConfig liveDomainDefaultConfig;

    public LiveDomainConfig save(LiveDomainConfig liveDomainConfig){
        return liveDomainConfigRepository.save(liveDomainConfig);
    }

    public LiveDomainConfig findByLine(Constant.LiveSourceLine line){
        return liveDomainConfigRepository.findByLine(line);
    }

    public void deleteAllDomainConfig() {
        liveDomainConfigRepository.deleteAll();
    }

    public List<LiveDomainConfig> findAll(){
        return liveDomainConfigRepository.findAll();
    }

    public void initDomainConfig(){
        for (Constant.LiveSourceLine line : Constant.LiveSourceLine.values()) {
            if(Objects.equals(Constant.LiveSourceLine.UNRECOGNIZED,line)) continue;
            if(findByLine(line) == null){
                logger.info("init live line domain :"+line);
                LiveDomainConfig liveDomainConfig = LiveDomainConfig.newInstance(line, liveDomainDefaultConfig.getDefaultDomainListByLine(line));
                save(liveDomainConfig);
            }
        }
    }

}
