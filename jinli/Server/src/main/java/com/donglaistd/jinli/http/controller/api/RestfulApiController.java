package com.donglaistd.jinli.http.controller.api;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.http.entity.DomainResult;
import com.donglaistd.jinli.http.entity.RechargeResult;
import com.donglaistd.jinli.http.entity.TokenRequestResult;
import com.donglaistd.jinli.http.entity.live.LiveInfo;
import com.donglaistd.jinli.service.DomainProcess;
import com.donglaistd.jinli.service.PlatformLiveService;
import com.donglaistd.jinli.service.UploadServerService;
import com.donglaistd.jinli.service.api.RestfulApiService;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.constant.CacheNameConstant.getPlatformTokenAccountKey;
import static com.donglaistd.jinli.constant.CacheNameConstant.getPlatformTokenKey;
import static com.donglaistd.jinli.constant.GameConstant.*;

@RestController
@RequestMapping("/api/v1")
public class RestfulApiController {
    private static final Logger logger = Logger.getLogger(RestfulApiController.class.getName());

    @Autowired
    private UserDaoService userDaoService;

    @Autowired
    private RedisTemplate<String, TokenRequestResult> redisTemplate;

    @Autowired
    UploadServerService uploadServerService;

    @Autowired
    PlatformLiveService plantLiveService;
    @Autowired
    RestfulApiService restfulApiService;
    @Autowired
    DomainProcess domainProcess;

    @Value("${client.resource.fileName}")
    private String clientResourceFileName;

    @RequestMapping(value = "/requestToken")
    @ResponseBody
    public TokenRequestResult handleRequest(String accountName, String disPlayName, Principal principal) {
        String platformName = principal.getName();
        if (Strings.isNullOrEmpty(accountName) || Strings.isNullOrEmpty(platformName)) {
            return new TokenRequestResult(RESTFUL_RESULT_MISSING_PARAMETER, "missing parameter");
        }
        int resultCode = restfulApiService.initOrUpdatePlantUser(accountName, disPlayName, principal);
        if(!Objects.equals(RESTFUL_RESULT_SUCCESS,resultCode)){
            return new TokenRequestResult(resultCode, "account displayName exist");
        }
        String gameAccountName = RestfulApiService.getAccountName(accountName, principal);
        var restfulResult = redisTemplate.opsForValue().get(getPlatformTokenAccountKey(gameAccountName));
        if(Objects.isNull(restfulResult) || restfulResult.getExpireTime() < Calendar.getInstance().getTimeInMillis()){
            restfulResult = restfulApiService.saveRequestTokenResult(RESTFUL_RESULT_SUCCESS, "success", principal, accountName);
        }
        return restfulResult;
    }

    @RequestMapping("/live")
    @ResponseBody
    public Object live(String token, String orientation, Principal principal) {
        if (StringUtils.isNullOrBlank(token)) {
            return new LiveInfo(RESTFUL_RESULT_MISSING_PARAMETER, "missing parameter");
        }
        //plantLiveService.initPlatformLive();
        //verify Token
        var restfulResult = redisTemplate.opsForValue().get(getPlatformTokenKey(token));
        if (restfulResult == null) {
            return new LiveInfo(RESTFUL_RESULT_TOKEN_EXPIRE, "token not exit or expire");
        }
        String gameAccountName = RestfulApiService.getAccountName(restfulResult.getAccountName(), principal);
        User plantUser = userDaoService.findByAccountName(gameAccountName);
        String liveUrl ="tMobile/"+clientResourceFileName+"/index.html?" +
                "webMobileType="+ Constant.WebMobileType.WEBMOBILE_PLATFORM_T.name()+
                "&accountName="+plantUser.getAccountName() +
                "&token="+restfulResult.getToken();
        logger.info("plant LiveUrl iS---->" + plantUser.getDisplayName());
        logger.info("H5 liveUrl is:"+ liveUrl);
        List<String> liveUrlList = domainProcess.getLiveUrlList(liveUrl);
        return new LiveInfo(RESTFUL_RESULT_SUCCESS, "SUCCESS", liveUrlList);
    }

    private boolean checkOrientationIsVertical(String orientation){
        return !Objects.equals(orientation, ORIENTATION_HORIZONTAL);
    }


    @RequestMapping("/rechargeGameCoin")
    public RechargeResult rechargeGameCoin(String token,String  platformAccountDisPlayName,String platformAccountUserId,Long rechargeAmount, Principal principal){
        //1.check param
        if (StringUtils.isNullOrBlank(token) || rechargeAmount ==null || rechargeAmount<= 0
                || StringUtils.isNullOrBlank(platformAccountDisPlayName) || StringUtils.isNullOrBlank(platformAccountUserId)) {
            return new RechargeResult(RESTFUL_RESULT_MISSING_PARAMETER, "missing parameter");
        }
        //2.check token
        var restfulResult = redisTemplate.opsForValue().get(getPlatformTokenKey(token));
        if(restfulResult == null)
            return new RechargeResult(RESTFUL_RESULT_TOKEN_EXPIRE, "token not exit or expire");
        //3.check user
        User user = restfulApiService.findPlantUserByAccountName(restfulResult.getAccountName(), principal);
        if (user == null) {
            return new RechargeResult(RESTFUL_RESULT_PLANT_ACCOUNT_NOT_EXIST, "account User not exist");
        }
        restfulApiService.rechargePlatformGameCoin(rechargeAmount,user,platformAccountUserId,platformAccountDisPlayName);
        return  new RechargeResult(RESTFUL_RESULT_SUCCESS, "SUCCESS");
    }

    @RequestMapping("/webMobileIndex")
    public String getWebMobileIndexAddress(){
        return "webMobile/" + clientResourceFileName + "/index.html?" + "webMobileType=" + Constant.WebMobileType.WEBMOBILE_NORMAL.name();
    }

    @RequestMapping("/getApiUrl")
    @ResponseBody
    public DomainResult getApiUrl(String token){
        if(StringUtils.isNullOrBlank(token))
            return new DomainResult(RESTFUL_RESULT_MISSING_PARAMETER, "missing parameter");
        var restfulResult = redisTemplate.opsForValue().get(getPlatformTokenKey(token));
        if(restfulResult == null) return new DomainResult(RESTFUL_RESULT_TOKEN_EXPIRE, "token not exit or expire");
        return new DomainResult(RESTFUL_RESULT_SUCCESS, "success",domainProcess.getApiUrl(Constant.PlatformType.PLATFORM_T));
    }

}
