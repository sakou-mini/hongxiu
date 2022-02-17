package com.donglai.web.process;

import com.donglai.model.db.entity.live.LiveDomain;
import com.donglai.model.db.entity.meta.LineSource;
import com.donglai.model.db.service.live.LiveDomainService;
import com.donglai.web.response.GlobalResponseCode;
import com.donglai.web.web.dto.request.UpdateLiveDomainRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglai.web.response.GlobalResponseCode.*;

@Component
public class CDNProcess {
    @Autowired
    LiveDomainService liveDomainService;



    public List<LiveDomain> getLiveDomainList(){
        return liveDomainService.findAll();
    }

    public GlobalResponseCode enableOrCloseCNDLine(int lineCode, boolean status){
        LiveDomain liveDomain = liveDomainService.findByLineCode(lineCode);
        if(Objects.isNull(liveDomain)) return CDN_NOT_EXISTS;
        liveDomain.setEnable(status);
        liveDomainService.save(liveDomain);
        return SUCCESS;
    }

    public GlobalResponseCode updateLiveDomain(UpdateLiveDomainRequest request) {
        if(CollectionUtils.isEmpty(request.getDomainList())) return CDN_DOMAIN_EMPTY;
        LiveDomain liveDomain = liveDomainService.findByLineCode(request.getLineCode());
        if(Objects.isNull(liveDomain)) return CDN_NOT_EXISTS;
        List<String> domainList = request.getDomainList().stream().map(String::trim).distinct().collect(Collectors.toList());
        liveDomain.setDomains(domainList);
        liveDomain.setUpdateTime(System.currentTimeMillis());
        liveDomainService.save(liveDomain);
        return SUCCESS;
    }

    //查询可用的cdn线路
    public List<LineSource> getCdnLine(){
        List<LiveDomain> liveDomainList = liveDomainService.findAll().stream().filter(LiveDomain::isEnable).collect(Collectors.toList());
        return liveDomainList.stream().map(liveDomain -> new LineSource(liveDomain.getLineCode())).collect(Collectors.toList());
    }
}
