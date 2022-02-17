package com.donglai.live.process;

import com.donglai.common.config.LiveDomainProperties;
import com.donglai.model.db.entity.live.LiveDomain;
import com.donglai.model.db.service.live.LiveDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglai.common.constant.LineSourceConstant.ALI_LINE;
import static com.donglai.common.constant.LineSourceConstant.WANGSU_LINE;

@Component
@Slf4j
public class LiveDomainProcess {
    @Autowired
    LiveDomainService liveDomainService;
    @Autowired
    LiveDomainProperties liveDomainProperties;

    public void initDomainConfig() {
        liveDomainService.deleteAllDomainConfig();
        for (int lineCode = WANGSU_LINE; lineCode < ALI_LINE; lineCode++) {
            if (liveDomainService.findByLineCode(lineCode) == null) {
                log.info("init live line domain :" + lineCode);
                LiveDomain liveDomainConfig = LiveDomain.newInstance(lineCode, liveDomainProperties.getDefaultDomainListByLine(lineCode));
                liveDomainService.save(liveDomainConfig);
            }
        }
    }
}
