package com.donglai.live.process;

import com.donglai.common.config.LiveDomainProperties;
import com.donglai.live.meta.LineSourceMetaBuilder;
import com.donglai.model.db.entity.live.LiveDomain;
import com.donglai.model.db.entity.meta.LineSource;
import com.donglai.model.db.service.live.LiveDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

import static com.donglai.common.constant.LineSourceConstant.ALI_LINE;
import static com.donglai.common.constant.LineSourceConstant.WANGSU_LINE;

@Component
@Slf4j
public class LiveDomainProcess {
    @Autowired
    LiveDomainService liveDomainService;
    @Autowired
    LiveDomainProperties liveDomainProperties;
    @Autowired
    LineSourceMetaBuilder lineSourceMetaBuilder;

    public void initDomainConfig() {
        liveDomainService.deleteAllDomainConfig();
        if(!CollectionUtils.isEmpty(liveDomainService.findAll())){
            log.info("已经初始化了线路域名");
            return;
        }
        //初始化CDN
        for (int lineCode = WANGSU_LINE; lineCode < ALI_LINE; lineCode++) {
            LineSource lineSource = lineSourceMetaBuilder.getById(lineCode);
            if (Objects.nonNull(lineSource) && liveDomainService.findByLineCode(lineCode) == null) {
                log.info("init live line domain :" + lineCode);
                LiveDomain liveDomainConfig = LiveDomain.newInstance(lineCode, lineSource.getHandlerClass(),liveDomainProperties.getDefaultDomainListByLine(lineCode));
                liveDomainService.save(liveDomainConfig);
            }
        }
    }
}
