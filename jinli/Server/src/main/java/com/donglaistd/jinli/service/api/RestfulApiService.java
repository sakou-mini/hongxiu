package com.donglaistd.jinli.service.api;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.UserBuilder;
import com.donglaistd.jinli.constant.PlatformConstant;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.dao.platform.PlatformRechargeRecordDaoService;
import com.donglaistd.jinli.database.entity.CoinFlow;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.plant.PlatformRechargeRecord;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.http.entity.TokenRequestResult;
import com.donglaistd.jinli.http.service.CoinFlowService;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.service.statistic.UserDataStatisticsProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.MessageUtil;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.donglaistd.jinli.Constant.PlatformType.PLATFORM_T;
import static com.donglaistd.jinli.constant.CacheNameConstant.getPlatformTokenAccountKey;
import static com.donglaistd.jinli.constant.CacheNameConstant.getPlatformTokenKey;
import static com.donglaistd.jinli.constant.GameConstant.*;

@Component
public class RestfulApiService {

    @Value("${token.expire.time.milliseconds}")
    public long tokenExpireTimeMilliseconds;

    @Autowired
    PlatformRechargeRecordDaoService platformRechargeRecordDaoService;

    @Autowired
    UserDaoService userDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    private RedisTemplate<String, TokenRequestResult> tokenRedisTemplate;
    @Value("${platform.coin.recharge.ratio}")
    private int rechargeRatio;
    @Autowired
    UserDataStatisticsProcess userDataStatisticsProcess;
    @Autowired
    CoinFlowService coinFlowService;

    public static String getAccountName(String accountName, Principal principal) {
        String platformName = principal.getName();
        return platformName + PLATFORMUSER_SPLITTER + accountName;
    }

    public TokenRequestResult saveRequestTokenResult(int result, String msg, Principal principal,String accountName){
        long expireTime = System.currentTimeMillis() + tokenExpireTimeMilliseconds;
        String platformName = principal.getName();
        TokenRequestResult tokenResult = new TokenRequestResult(result, msg, platformName, UUID.randomUUID().toString(), accountName, expireTime);
        String gameAccountName = getAccountName(accountName, principal);
        return saveToken(gameAccountName, tokenResult, tokenExpireTimeMilliseconds);
    }

    public TokenRequestResult saveToken( String accountName, TokenRequestResult tokenResult,long expireTime){
        String accountTokenKey = getPlatformTokenAccountKey(accountName);
        String tokenKey = getPlatformTokenKey(tokenResult.getToken());
        tokenRedisTemplate.opsForValue().set(accountTokenKey,tokenResult,expireTime, TimeUnit.MILLISECONDS);
        tokenRedisTemplate.opsForValue().set(tokenKey,tokenResult,expireTime, TimeUnit.MILLISECONDS);
        return tokenResult;
    }

    public int initOrUpdatePlantUser(String accountName, String disPlayName, Principal principal){
        String platformUserId = PlatformConstant.getPlatformUserIdByPlatform(PLATFORM_T, accountName);
        String gameAccountName = getAccountName(accountName, principal);
        var user = userDaoService.findByAccountName(gameAccountName);
        if(!StringUtils.isNullOrBlank(disPlayName) && userDaoService.existsByDisplayNameAndAccountNameIsNot(disPlayName,gameAccountName))
            return RESTFUL_RESULT_DISPLAY_EXIST;
        if (Objects.isNull(user)) {
            user = userBuilder.createPlatformUser(gameAccountName, "", "", 0, PLATFORM_T,platformUserId);
            userDataStatisticsProcess.totalRegisterUserDataStatistics(Jinli.RegisterRequest.newBuilder().setMobileModel("other").build(),user.getId(),PLATFORM_T);
        }
        if(!StringUtils.isNullOrBlank(disPlayName)) {
            user.setDisplayName(disPlayName);
        }
        if(StringUtils.isNullOrBlank(user.getPlatformUserId())){
            user.setPlatformUserId(platformUserId);
        }
        dataManager.saveUser(user);
        return RESTFUL_RESULT_SUCCESS;
    }

    public User findPlantUserByAccountName(String accountName,Principal principal){
        String gameAccountName = getAccountName(accountName, principal);
        return userDaoService.findByAccountName(gameAccountName);
    }


    public void rechargePlatformGameCoin(long rechargeAmount,User user,String platformAccountUserId,String  platformAccountDisPlayName){
        long rechargeGameCoin = rechargeAmount * rechargeRatio;
        CoinFlow coinFlow = coinFlowService.setUserRecharge(user.getId(), rechargeAmount);
        EventPublisher.publish(new ModifyCoinEvent(user,rechargeGameCoin));
        user = userDaoService.findById(user.getId());
        PlatformRechargeRecord rechargeRecord = PlatformRechargeRecord.newInstance(user.getId(), platformAccountUserId, platformAccountDisPlayName, rechargeAmount, rechargeGameCoin,
                user.getGameCoin(),coinFlow.getRecharge());
        platformRechargeRecordDaoService.save(rechargeRecord);
        user = dataManager.findUser(user.getId());
        Jinli.PlatformRechargeBroadcastMessage.Builder rechargeMessage = Jinli.PlatformRechargeBroadcastMessage.newBuilder().setGameCoin(user.getGameCoin())
                .setRechargeCoin(rechargeGameCoin).setUserId(user.getId()).setDisplayName(user.getDisplayName());
        MessageUtil.sendMessage(user.getId(),MessageUtil.buildReply(rechargeMessage));
    }
}
