package com.donglai.model.db.service.common;

import com.donglai.model.db.entity.common.ServerProperty;
import com.donglai.model.db.repository.common.ServerPropertyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.common.constant.CommonConstant.SERVER_ID;

@Service
@Slf4j
public class ServerPropertyService {
    @Autowired
    ServerPropertyRepository repository;
    @Autowired
    ServerPropertyService serverPropertyService;

    public ServerProperty getServerProperty(){
        return repository.findById(SERVER_ID).orElse(null);
    }

    public ServerProperty save(ServerProperty serverProperty){
        return repository.save(serverProperty);
    }
    public void initServerPropertyService(){
        ServerProperty serverProperty = getServerProperty();
        if (Objects.isNull(serverProperty)) {
            log.info("初始化了服务器属性");
            serverPropertyService.save(ServerProperty.newInstance());
        }
    }
}
