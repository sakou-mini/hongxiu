package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.DomainLine;
import com.donglaistd.jinli.constant.DomainStatue;
import com.donglaistd.jinli.database.dao.system.domainConfig.DomainConfigDaosService;
import com.donglaistd.jinli.database.entity.system.domainConfig.DomainConfig;
import com.donglaistd.jinli.http.entity.DomainInfo;
import com.donglaistd.jinli.service.DomainProcess;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class DomainProcessTest extends BaseTest {
    @Autowired
    DomainProcess domainProcess;
    @Autowired
    DomainConfigDaosService domainConfigDaosService;
    @Test
    public void TestCheckDomainIsAvailable(){
        domainProcess.addDomain("baodi.com", DomainLine.INLAND,"", Constant.PlatformType.PLATFORM_T);
        domainProcess.addDomain("jinli.live", DomainLine.INLAND,"", Constant.PlatformType.PLATFORM_T);
        domainProcess.addDomain("ba23odi.com", DomainLine.INLAND,"", Constant.PlatformType.PLATFORM_T);
        domainProcess.checkDomainIsAvailable();
        Map<DomainLine, List<DomainInfo>> domainList = domainProcess.getDomainList(Constant.PlatformType.PLATFORM_T);
        DomainConfig domainConfig = domainConfigDaosService.findByDomainNameByPlatform("baodi.com",Constant.PlatformType.PLATFORM_T);
        Assert.assertEquals(DomainStatue.UNUSABLE,domainConfig.getStatue());
        domainConfig = domainConfigDaosService.findByDomainNameByPlatform("jinli.live",Constant.PlatformType.PLATFORM_T);
        Assert.assertEquals(DomainStatue.NORMAL,domainConfig.getStatue());
        domainConfig = domainConfigDaosService.findByDomainNameByPlatform("ba23odi.com",Constant.PlatformType.PLATFORM_T);
        Assert.assertEquals(DomainStatue.UNUSABLE,domainConfig.getStatue());

    }
}
