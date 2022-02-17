package com.donglai.live.message.services.queue.handler;

import com.donglai.common.constant.PathConstant;
import com.donglai.live.process.QueueProcess;
import com.donglai.common.util.HttpUtil;
import com.donglai.model.db.entity.common.H5DomainConfig;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.service.common.H5DomainConfigService;
import com.donglai.model.db.service.common.QueueExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
@Slf4j
public class MinuteJobHandler implements TriggerHandler {
    @Autowired
    QueueProcess queueProcess;
    @Autowired
    QueueExecuteService queueExecuteService;
    @Autowired
    H5DomainConfigService h5DomainConfigService;


    @Override
    public void deal(QueueExecute queueExecute) {
        log.info("执行每分钟任务");
        queueExecuteService.del(queueExecute);
        queueProcess.initMinuteJobQueue();
        checkH5DomainIsAvailable();
    }

    @Transactional
    public void checkH5DomainIsAvailable(){
        List<H5DomainConfig> h5DomainConfigList = h5DomainConfigService.findAll();
        String url ;
        for (H5DomainConfig h5DomainConfig : h5DomainConfigList) {
            //没有格式化,默认添加域名时需要加上http 或者https
            url = h5DomainConfig.getDomainName() + PathConstant.H5_DOMAIN_TEST_PATH;
            if(!HttpUtil.verifyHostIsAvailable(url)){
                log.warn("{} notAvailable,please check it~~~~~~",h5DomainConfig.getDomainName());
                h5DomainConfig.setStatus(false);
                h5DomainConfigService.save(h5DomainConfig);
            }else if(!h5DomainConfig.isStatus()){
                h5DomainConfig.setStatus(true);
                h5DomainConfigService.save(h5DomainConfig);
            }
        }
    }
}
