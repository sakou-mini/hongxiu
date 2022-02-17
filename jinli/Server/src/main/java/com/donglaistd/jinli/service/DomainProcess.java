package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.BackOfficeConstant;
import com.donglaistd.jinli.constant.DomainLine;
import com.donglaistd.jinli.constant.DomainStatue;
import com.donglaistd.jinli.database.dao.BackOfficeUserDaoService;
import com.donglaistd.jinli.database.dao.system.LiveDomainConfigDaoRecordService;
import com.donglaistd.jinli.database.dao.system.LiveDomainConfigDaoService;
import com.donglaistd.jinli.database.dao.system.domainConfig.DomainConfigDaosService;
import com.donglaistd.jinli.database.dao.system.domainConfig.DomainConfigRecordDaoService;
import com.donglaistd.jinli.database.dao.system.domainConfig.DomainViewRecordDaoService;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import com.donglaistd.jinli.database.entity.system.LiveDomainConfig;
import com.donglaistd.jinli.database.entity.system.LiveDomainConfigRecord;
import com.donglaistd.jinli.database.entity.system.domainConfig.DomainConfig;
import com.donglaistd.jinli.database.entity.system.domainConfig.DomainConfigRecord;
import com.donglaistd.jinli.database.entity.system.domainConfig.DomainViewRecord;
import com.donglaistd.jinli.http.entity.DomainInfo;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.HttpUtil;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.GameConstant.*;

@Component
public class DomainProcess {
    private static final Logger LOGGER = Logger.getLogger(DomainProcess.class.getName());

    @Autowired
    DomainConfigDaosService domainConfigDaosService;
    @Autowired
    DomainViewRecordDaoService domainViewRecordDaoService;
    @Autowired
    DomainConfigRecordDaoService domainConfigRecordDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    HttpUtil httpUtil;
    @Autowired
    LiveDomainConfigDaoService liveDomainConfigDaoService;
    @Autowired
    LiveDomainConfigDaoRecordService liveDomainConfigDaoRecordService;
    @Autowired
    BackOfficeUserDaoService backOfficeUserDaoService;

    public static Map<String,Integer> getDomainNum() {
        Map<String, Integer> domainCount = new HashMap<>();
        String domain ;
        for (Channel channel : DataManager.userChannel.values()) {
            domain = DataManager.getDomainKeyFromChannel(channel);
            if(!StringUtils.isNullOrBlank(domain)){
                domainCount.compute(domain, (k, v) -> {
                    if(v == null) v = 0;
                    return v = v + 1;
                });
            }
        }
        return domainCount;
    }

    public Map<DomainLine, List<DomainInfo>> getDomainList(Constant.PlatformType platformType){
        Map<DomainLine, List<DomainInfo>> domainLineListMap = new HashMap<>();
        List<DomainConfig> allDomain = domainConfigDaosService.findByPlatformType(platformType);
        Map<String, Integer> domainNum = getDomainNum();
        long endTime = System.currentTimeMillis();
        long startTime = endTime - TimeUnit.HOURS.toMillis(1);
        long hourViewNum;
        for (DomainConfig domainConfig : allDomain) {
            hourViewNum = domainViewRecordDaoService.totalViewNumByTime(domainConfig.getDomainName(), startTime, endTime);
            List<DomainInfo> domainInfos = domainLineListMap.computeIfAbsent(domainConfig.getLine(), k -> new ArrayList<>());
            domainInfos.add(new DomainInfo(domainConfig, hourViewNum, domainNum.getOrDefault(domainConfig.getDomainName(),  0)));
        }
        return domainLineListMap;
    }

    public boolean updateDomainConfig(String oldDomainName, String newDomainName, String backofficeName,Constant.PlatformType platformType) {
        DomainConfig domainConfig = domainConfigDaosService.findByDomainNameByPlatform(oldDomainName,platformType);
        DomainConfig newDomainConfig = domainConfigDaosService.findByDomainNameByPlatform(newDomainName, platformType);
        if (Objects.isNull(domainConfig) || StringUtils.isNullOrBlank(newDomainName) || newDomainConfig!=null) return false;
        domainConfig.setDomainName(newDomainName);
        domainConfig.setCreateTime(System.currentTimeMillis());
        domainConfigDaosService.save(domainConfig);
        DomainConfigRecord configRecord = new DomainConfigRecord(domainConfig.getLine(), oldDomainName, newDomainName,backofficeName, platformType);
        domainConfigRecordDaoService.save(configRecord);
        return true;
    }

    public int addDomain(String domain, DomainLine line, String backofficeName, Constant.PlatformType platformType) {
        if(StringUtils.isNullOrBlank(domain) || Objects.isNull(line))
            return DOMAIN_PARAM_ERROR;
        if(domainConfigDaosService.findByDomainNameByPlatform(domain, platformType) != null)
            return DOMAIN_EXIT;
        if(domainConfigDaosService.countByDomainLine(line,platformType) >= MAX_DOMAIN_NUM)
            return DOMAIN_NUM_OVERLIMIT;
        DomainConfig domainConfig = new DomainConfig(domain, DomainStatue.NORMAL, line,platformType);
        DomainConfigRecord domainConfigRecord = new DomainConfigRecord(line, domain, backofficeName, platformType);
        domainConfigDaosService.save(domainConfig);
        domainConfigRecordDaoService.save(domainConfigRecord);
        return SUCCESS;
    }

    public void saveDomainViewRecord(String userId, String domainName, Constant.PlatformType platformType, ChannelHandlerContext ctx) {
        if(StringUtils.isNullOrBlank(domainName)) return;
        String rootDomain = StringUtils.getRootDomain(domainName);
        if(StringUtils.isNullOrBlank(rootDomain)) return;
        DataManager.saveDomainKeyToChannel(ctx,rootDomain);
        if(platformType == null) return;
        domainViewRecordDaoService.save(new DomainViewRecord(rootDomain, userId,platformType));
    }

    public void checkDomainIsAvailable(){
        List<DomainConfig> domain= domainConfigDaosService.findAll();
        String url ;
        for (DomainConfig domainConfig : domain) {
            url = String.format("https://%s.%s%s", API, domainConfig.getDomainName(), CONNECTED_PATH);
            if(!httpUtil.verifyHostIsAvailable(url)){
                LOGGER.warning(domainConfig.getDomainName() +" notAvailable,please check it~~~~~~");
                domainConfigDaosService.updateDomainStatue(domainConfig.getDomainName(),domainConfig.getPlatformType(),DomainStatue.UNUSABLE);
            } else if(Objects.equals(domainConfig.getStatue(),DomainStatue.UNUSABLE)) {
                domainConfigDaosService.updateDomainStatue(domainConfig.getDomainName(),domainConfig.getPlatformType(), DomainStatue.NORMAL);
            }
        }
    }

    public List<String> getApiUrl(Constant.PlatformType platformType){
        return domainConfigDaosService.findPlatformNormalUrl(platformType).stream().map(domainConfig -> EGW + "." + domainConfig.getDomainName()).collect(Collectors.toList());
    }

    public List<String> getLiveUrlList(String liveUrl) {
        List<String> urls = new ArrayList<>();
        String url;
        List<DomainConfig> domains = domainConfigDaosService.findByPlatformType(Constant.PlatformType.PLATFORM_T);
        for (DomainConfig domainConfig : domains) {
            url = String.format("https://%s.%s%s", H5, domainConfig.getDomainName(), "/"+liveUrl);
            urls.add(url);
        }
        if(urls.isEmpty()) {
            urls.add(liveUrl);
            LOGGER.warning("empty domain config");
        }
        return urls;
    }

    public PageInfo<DomainConfigRecord> domainConfigRecordPageInfo(int page, int size, Constant.PlatformType platformType){
        PageRequest pageRequest = PageRequest.of(page, size);
        return domainConfigRecordDaoService.domainConfigRecordPageInfo(pageRequest,platformType);
    }

    public void deleteDomain(String domainName, Constant.PlatformType platformType){
        domainConfigDaosService.deleteByDomain(domainName,platformType);
    }

    //TODO DELETE ,only in clean date used
    public void initPlatformDomain(){
        List<DomainConfig> t_domains = domainConfigDaosService.findByPlatformType(Constant.PlatformType.PLATFORM_T);
        if(t_domains.isEmpty()){
            addDomain("jsjiws.bar", DomainLine.INLAND, BackOfficeConstant.ROOT_ACCOUNT, Constant.PlatformType.PLATFORM_T);
            addDomain("renquanquan.com", DomainLine.INLAND, BackOfficeConstant.ROOT_ACCOUNT, Constant.PlatformType.PLATFORM_T);
            addDomain("chuanyangsy.com", DomainLine.SOUTHEAST_ASIA, BackOfficeConstant.ROOT_ACCOUNT, Constant.PlatformType.PLATFORM_T);
            addDomain("jsndfj.bar", DomainLine.SOUTHEAST_ASIA, BackOfficeConstant.ROOT_ACCOUNT, Constant.PlatformType.PLATFORM_T);
            addDomain("uiha.bar", DomainLine.OVERSEAS, BackOfficeConstant.ROOT_ACCOUNT, Constant.PlatformType.PLATFORM_T);
            addDomain("kajsh.bar", DomainLine.OVERSEAS, BackOfficeConstant.ROOT_ACCOUNT, Constant.PlatformType.PLATFORM_T);
        }
        List<DomainConfig> q_domains = domainConfigDaosService.findByPlatformType(Constant.PlatformType.PLATFORM_Q);
        if(q_domains.isEmpty()){
            addDomain("nongyedata.com", DomainLine.INLAND, BackOfficeConstant.ROOT_ACCOUNT, Constant.PlatformType.PLATFORM_Q);

            addDomain("sdfvy.bar", DomainLine.SOUTHEAST_ASIA, BackOfficeConstant.ROOT_ACCOUNT, Constant.PlatformType.PLATFORM_Q);
            addDomain("sdrgg.bar", DomainLine.SOUTHEAST_ASIA, BackOfficeConstant.ROOT_ACCOUNT, Constant.PlatformType.PLATFORM_Q);
            addDomain("qweer.bar", DomainLine.SOUTHEAST_ASIA, BackOfficeConstant.ROOT_ACCOUNT, Constant.PlatformType.PLATFORM_Q);

            addDomain("nvjndj.bar", DomainLine.OVERSEAS, BackOfficeConstant.ROOT_ACCOUNT, Constant.PlatformType.PLATFORM_Q);
            addDomain("kjasjn.bar", DomainLine.OVERSEAS, BackOfficeConstant.ROOT_ACCOUNT, Constant.PlatformType.PLATFORM_Q);
            addDomain("dfgrg.bar", DomainLine.OVERSEAS, BackOfficeConstant.ROOT_ACCOUNT, Constant.PlatformType.PLATFORM_Q);
        }
    }

    //=====================live domain process===========================
    public List<LiveDomainConfig> queryLiveDomainList(){
        return liveDomainConfigDaoService.findAll();
    }

    public void updateLiveDomain(Constant.LiveSourceLine line , List<String> liveDomainList, String backofficeUserName){
        BackOfficeUser backOfficeUser = backOfficeUserDaoService.findByAccountName(backofficeUserName);
        LiveDomainConfig liveDomainConfig = Optional.ofNullable(liveDomainConfigDaoService.findByLine(line)).orElse(LiveDomainConfig.newInstance(line, new ArrayList<>(0)));
        LiveDomainConfigRecord configRecord = LiveDomainConfigRecord.newInstance(liveDomainConfig.getDomains(), liveDomainList, backOfficeUser.getId());
        liveDomainConfig.setDomains(liveDomainList);
        liveDomainConfigDaoService.save(liveDomainConfig);
        liveDomainConfigDaoRecordService.save(configRecord);
    }

    public PageInfo<LiveDomainConfigRecord> queryLiveDomainConfigRecord(int page, int size){
        return liveDomainConfigDaoRecordService.domainConfigRecordPageInfo(PageRequest.of(page - 1, size));
    }
}