package com.donglai.web.process;

import com.donglai.common.constant.DomainArea;
import com.donglai.common.service.RedisService;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.H5DomainConfig;
import com.donglai.model.db.service.common.H5DomainConfigService;
import com.donglai.model.db.service.common.H5DomainViewRecordService;
import com.donglai.web.response.GlobalResponseCode;
import com.donglai.web.web.dto.request.H5DomainSetRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.donglai.common.constant.CommonConstant.MAX_DOMAIN_NUM;
import static com.donglai.web.response.GlobalResponseCode.*;

@Component
public class H5DomainProcess {
    @Autowired
    H5DomainConfigService h5DomainConfigService;
    @Autowired
    RedisService redisService;
    @Autowired
    H5DomainViewRecordService h5DomainViewRecordService;


    /*1.获取域名列表*/
    public Map<DomainArea, List<H5DomainConfig>> getDomainList(){
        Map<DomainArea, List<H5DomainConfig>> domainLineListMap = new HashMap<>();
        List<H5DomainConfig> domainConfigs = h5DomainConfigService.findAll();
        long endTime = System.currentTimeMillis();
        long startTime = endTime - TimeUnit.HOURS.toMillis(1);
        long hourViewNum;
        long currentViewNum;
        for (H5DomainConfig h5DomainConfig : domainConfigs) {
            domainLineListMap.computeIfAbsent(h5DomainConfig.getLine(), k -> new ArrayList<>()).add(h5DomainConfig);
        }
        return domainLineListMap;
    }

    /*2.修改域名*/
    public GlobalResponseCode updateDomainName(H5DomainSetRequest request){
        H5DomainConfig currentDomain = h5DomainConfigService.findById(request.getId());
        if(StringUtils.isNullOrBlank(request.getDomain()))
            return DOMAIN_ILLEGALITY;
        if(Objects.isNull(currentDomain))
            return DOMAIN_NOT_EXISTS;
        String domain = request.getDomain();
        if(!domain.startsWith("http"))
            domain = String.format("http://%s",domain);
        H5DomainConfig otherDomain = h5DomainConfigService.findByDomainName(domain);
        if(Objects.nonNull(otherDomain) && !Objects.equals(otherDomain.getId(),currentDomain.getId()))
            return DOMAIN_REPETITION;

        currentDomain.setDomainName(domain);
        currentDomain.setCreateTime(System.currentTimeMillis());
        currentDomain.setStatus(true);
        h5DomainConfigService.save(currentDomain);
        return SUCCESS;
    }

    /*3.添加域名*/
    public GlobalResponseCode addH5Domain(String domain, DomainArea line){
        if(StringUtils.isNullOrBlank(domain) || Objects.isNull(line))
            return PARAM_ERROR;
        if(!domain.startsWith("http"))
            domain = String.format("http://%s",domain);
        if(h5DomainConfigService.findByDomainName(domain) != null)
            return DOMAIN_REPETITION;
        if(h5DomainConfigService.countByDomainLine(line) >= MAX_DOMAIN_NUM)
            return DOMAIN_NUM_OVER_LIMIT;
        H5DomainConfig domainConfig = H5DomainConfig.newInstance(domain, line);
        h5DomainConfigService.save(domainConfig);
        return SUCCESS;
    }

    /*4.删除直播域名*/
    public void deleteH5Domain(long id){
        h5DomainConfigService.deleteById(id);
    }
}
