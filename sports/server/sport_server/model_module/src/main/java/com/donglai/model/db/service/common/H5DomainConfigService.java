package com.donglai.model.db.service.common;

import com.donglai.common.constant.DomainArea;
import com.donglai.model.db.entity.common.H5DomainConfig;
import com.donglai.model.db.repository.common.H5DomainConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class H5DomainConfigService {
    @Autowired
    H5DomainConfigRepository repository;

    public List<H5DomainConfig> findAll(){
        return repository.findAll();
    }

    public H5DomainConfig save(H5DomainConfig domainConfig){
        return repository.save(domainConfig);
    }

    public List<H5DomainConfig> saveAll(List<H5DomainConfig> domainConfigs){
        return repository.saveAll(domainConfigs);
    }

    public void deleteAll(){
        repository.deleteAll();
    }

    public void deleteById(long id){
        repository.deleteById(id);
    }

    public H5DomainConfig findById(long id){
        return repository.findById(id).orElse(null);
    }

    public H5DomainConfig findByDomainName(String domain){
        return repository.findByDomainName(domain);
    }

    public long countByDomainLine(DomainArea line) {
        return repository.countByLine(line);
    }
}
