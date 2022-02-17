package com.donglaistd.jinli.database.dao.system.domainConfig;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.DomainLine;
import com.donglaistd.jinli.constant.DomainStatue;
import com.donglaistd.jinli.database.entity.system.domainConfig.DomainConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DomainConfigDaosService {
    @Autowired
    DomainConfigRepository domainConfigRepository;
    @Autowired
    MongoOperations mongoOperations;

    public List<DomainConfig> findAll(){
        return domainConfigRepository.findAll();
    }

    public List<DomainConfig> findByPlatformType(Constant.PlatformType platformType){
        return domainConfigRepository.findByPlatformType(platformType);
    }

    public List<DomainConfig> findPlatformNormalUrl(Constant.PlatformType platformType){
        return domainConfigRepository.findAllByStatueIsAndPlatformTypeIs(DomainStatue.NORMAL, platformType);
    }

    public DomainConfig save(DomainConfig domainConfig){
        return domainConfigRepository.save(domainConfig);
    }

    public DomainConfig findById(long id){
        return domainConfigRepository.findById(id).orElse(null);
    }

    public DomainConfig findByDomainNameByPlatform(String domainName, Constant.PlatformType platformType){
        return domainConfigRepository.findByDomainNameAndPlatformType(domainName, platformType);
    }

    public void updateDomainStatue(String domainName, Constant.PlatformType platformType,  DomainStatue statue) {
        mongoOperations.updateFirst(Query.query(Criteria.where("domainName").is(domainName).and("platformType").is(platformType)), new Update().set("statue", statue), DomainConfig.class);
    }

    public long countByDomainLine(DomainLine line , Constant.PlatformType platformType) {
        return domainConfigRepository.countByLineAndPlatformType(line,platformType);
    }

    public void deleteByDomain(String domainName, Constant.PlatformType platformType){
        domainConfigRepository.deleteByDomainNameAndPlatformType(domainName,platformType);
    }

    public void deleteEmptyPlatformDomainConfig(){
        domainConfigRepository.deleteByPlatformTypeIsNull();
    }
}
