package com.donglaistd.jinli.database.dao.system.domainConfig;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.DomainLine;
import com.donglaistd.jinli.constant.DomainStatue;
import com.donglaistd.jinli.database.entity.system.domainConfig.DomainConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DomainConfigRepository extends MongoRepository<DomainConfig, Long> {
    DomainConfig findByDomainNameAndPlatformType(String domainName, Constant.PlatformType platformType);

    List<DomainConfig> findAllByStatueIsAndPlatformTypeIs(DomainStatue statue,Constant.PlatformType platformType);

    long countByLineAndPlatformType(DomainLine line, Constant.PlatformType platformType);

    void deleteByDomainNameAndPlatformType(String domainName, Constant.PlatformType platformType);

    List<DomainConfig> findByPlatformType(Constant.PlatformType platformType);

    void deleteByPlatformTypeIsNull();
}
