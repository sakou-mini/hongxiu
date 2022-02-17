package com.donglai.model.config.es;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.model.service.IElasticsearchService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Order(value = 1)
public class EsSetUp implements CommandLineRunner{

    @Override
    public void run(String... args) throws Exception {
        var esBean = SpringApplicationContext.getBeanOfType(IElasticsearchService.class);
        esBean.values().forEach(IElasticsearchService::synCreatIndex);
    }

}
