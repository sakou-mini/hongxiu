package com.donglaistd.jinli.database.dao.system;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.system.SystemMessageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
@Transactional
public class SystemMessageConfigDaoService {

    @Autowired
    SystemMessageConfigRepository systemMessageConfigRepository;

    public List<SystemMessageConfig> findAll(){
        return systemMessageConfigRepository.findAll();
    }

    public SystemMessageConfig findSystemMessage(Constant.PlatformType platformType){
        return systemMessageConfigRepository.findById(String.valueOf(platformType.getNumber())).orElse(SystemMessageConfig.newInstance(platformType));
    }

    public SystemMessageConfig save(SystemMessageConfig systemMessageConfig){
        return systemMessageConfigRepository.save(systemMessageConfig);
    }

}
