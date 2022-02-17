package com.donglai.model.db.repository.common;

import com.donglai.common.constant.DomainArea;
import com.donglai.model.db.entity.common.H5DomainConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface H5DomainConfigRepository extends MongoRepository<H5DomainConfig,Long> {
    H5DomainConfig findByDomainName(String domainName);

    long countByLine(DomainArea line);
}
