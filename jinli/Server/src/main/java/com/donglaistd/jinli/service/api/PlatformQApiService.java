package com.donglaistd.jinli.service.api;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.QPlatformGameConfigBuilder;
import com.donglaistd.jinli.builder.UserBuilder;
import com.donglaistd.jinli.constant.PlatformConstant;
import com.donglaistd.jinli.database.dao.UserAttributeDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.dao.system.domainConfig.DomainConfigDaosService;
import com.donglaistd.jinli.database.entity.*;
import com.donglaistd.jinli.database.entity.statistic.record.UserRoomRecord;
import com.donglaistd.jinli.http.entity.PlatformQRoomInfo;
import com.donglaistd.jinli.http.entity.TokenRequestResult;
import com.donglaistd.jinli.service.statistic.UserDataStatisticsProcess;
import com.donglaistd.jinli.util.ComparatorUtil;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.PlatformType.PLATFORM_Q;
import static com.donglaistd.jinli.constant.CacheNameConstant.getPlatformTokenAccountKey;
import static com.donglaistd.jinli.constant.CacheNameConstant.getPlatformTokenKey;
import static com.donglaistd.jinli.constant.GameConstant.*;
import static com.donglaistd.jinli.constant.LiveConstant.IMAGE_RESOURCE_API_PATH;
import static com.donglaistd.jinli.constant.PlatformConstant.Q_PLATFORM_H5_PATH;
import static com.donglaistd.jinli.service.RoomProcess.buildMuteChatBroadcastMessage;
import static com.donglaistd.jinli.service.RoomProcess.buildUnMuteChatBroadcastMessage;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class PlatformQApiService {
    @Value("${upload.nodes}")
    private final List<String> uploadServerNodes = new ArrayList<>();
    @Value("${token.expire.time.milliseconds}")
    public long tokenExpireTimeMilliseconds;
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    UserDataStatisticsProcess userDataStatisticsProcess;
    @Autowired
    DataManager dataManager;
    @Autowired
    DomainConfigDaosService domainConfigDaosService;
    @Autowired
    private RedisTemplate<String, TokenRequestResult> tokenRedisTemplate;
    @Autowired
    QPlatformGameConfigBuilder qPlatformGameConfigBuilder;
    @Autowired
    UserAttributeDaoService userAttributeDaoService;
    @Autowired
    RestfulApiService restfulApiService;

    public static String getAccountName(String accountName, String platformName) {
        return platformName + PLATFORMUSER_SPLITTER + accountName;
    }

    public int initPlatformUser(String accountName, String displayName, String userAvatar, String platformName,boolean mute){
        String platformUserId = PlatformConstant.getPlatformUserIdByPlatform(PLATFORM_Q, accountName);
        if(!StringUtils.isNullOrBlank(displayName))
            displayName = PLATFOR_NAME_Q_SPLITTER + displayName;
        String jinliAccountName = getAccountName(accountName, platformName);
        if(!StringUtils.isNullOrBlank(displayName) && userDaoService.existsByDisplayNameAndAccountNameIsNot(displayName,jinliAccountName))
            return RESTFUL_RESULT_DISPLAY_EXIST;
        var user = userDaoService.findByAccountName(jinliAccountName);
        if (Objects.isNull(user)) {
            user = userBuilder.createPlatformUser(jinliAccountName, StringUtils.generateTouristName(userDaoService.count()), "", 0, PLATFORM_Q,platformUserId);
            userDataStatisticsProcess.totalRegisterUserDataStatistics(Jinli.RegisterRequest.newBuilder().setMobileModel("other").build(),user.getId(), PLATFORM_Q);
        }
        if(!StringUtils.isNullOrBlank(displayName)) {
            user.setDisplayName(displayName);
        }
        if(!StringUtils.isNullOrBlank(userAvatar)) {
            user.setAvatarUrl(userAvatar);
        }
        user.setPlatformUserId(platformUserId);
        dataManager.saveUser(user);
        UserAttribute userAttribute = userAttributeDaoService.findByUserIdOrSaveIfNotExit(user.getId());
        if(!Objects.equals(mute,userAttribute.isPlatformMute())){
            userAttribute.setPlatformMute(mute);
            broadCastMuteChatIfInRoom(user, mute);
            userAttributeDaoService.save(userAttribute);
        }
        return RESTFUL_RESULT_SUCCESS;
    }

    private void broadCastMuteChatIfInRoom(User user,boolean isMute){
        UserRoomRecord userRoomRecord = DataManager.getUserRoomRecord(user.getId());
        if(!StringUtils.isNullOrBlank(userRoomRecord.getRoomId())){
            Room onlineRoom = DataManager.findOnlineRoom(userRoomRecord.getRoomId());
            if(Objects.nonNull(onlineRoom)){
                Jinli.MuteChatBroadcastMessage message;
                if(isMute) {
                    MuteProperty muteProperty = MuteProperty.newInstance(Constant.MuteTimeType.MUTE_TIME_DEFAULT, Constant.MuteReason.MUTE_OTHER,
                            "", Constant.MuteIdentity.IDENTITY_NULL, null, Constant.MuteArea.ALL_ROOM);
                    message = buildMuteChatBroadcastMessage(user.getId(), muteProperty);
                }else {
                    message = buildUnMuteChatBroadcastMessage(user.getId(), Constant.MuteIdentity.IDENTITY_NULL);
                }
                onlineRoom.broadCastToAllPlatform(buildReply(message));
            }
        }
    }

    public TokenRequestResult getTokenResult(String accountName,String platformName){
        String jinliAccountName = getAccountName(accountName, platformName);
        var restfulResult = tokenRedisTemplate.opsForValue().get(getPlatformTokenAccountKey(jinliAccountName));
        var now = System.currentTimeMillis();
        if (Objects.isNull(restfulResult) || restfulResult.getExpireTime() < now){
            long expireTime = System.currentTimeMillis() + tokenExpireTimeMilliseconds;
            TokenRequestResult tokenResult = new TokenRequestResult(RESTFUL_RESULT_SUCCESS, "success", platformName, UUID.randomUUID().toString(), accountName, expireTime);
            restfulResult = restfulApiService.saveToken(jinliAccountName, tokenResult, tokenExpireTimeMilliseconds);
        }
        return restfulResult;
    }

    public TokenRequestResult getTokenResultByToken(String token){
        String platformTokenKey = getPlatformTokenKey(token);
        return tokenRedisTemplate.opsForValue().get(platformTokenKey);
    }

    public List<PlatformQRoomInfo> findPlatformQLiveRoom(TokenRequestResult tokenResult){
        List<PlatformQRoomInfo> platformQRoomInfos = new ArrayList<>();
        //List<String> domainList = domainConfigDaosService.findPlatformNormalUrl(Constant.PlatformType.PLATFORM_Q).stream().map(DomainConfig::getDomainName).collect(Collectors.toList());
        String jinliAccountName = getAccountName(tokenResult.getAccountName(), tokenResult.getPlatformName());
        List<Room> rooms = DataManager.getOnlineRoomList().stream().filter(room -> room.isLive() && room.isSharedToPlatform(PLATFORM_Q))
                .sorted(ComparatorUtil.getRoomRecommendComparator(PLATFORM_Q)).collect(Collectors.toList());
        Collections.reverse(rooms);
        LiveUser liveUser;
        User liveUserUserInfo;
        String avatarUrl;
        //String domain = domainList.isEmpty() ? "http://"+uploadServerNodes.get(0) : "http://"+ H5 +"." + domainList.get(0);
        //String domain = "http://"+uploadServerNodes.get(0);
        for (Room room : rooms) {
            liveUser = dataManager.findLiveUser(room.getLiveUserId());
            liveUserUserInfo = userDaoService.findById(liveUser.getUserId());
            avatarUrl = IMAGE_RESOURCE_API_PATH + liveUserUserInfo.getAvatarUrl();
            String url = Q_PLATFORM_H5_PATH+"?webMobileType=" + Constant.WebMobileType.WEBMOBILE_PLATFORM_Q.name() +
                    "&roomId=" + room.getId() +
                    "&accountName=" + jinliAccountName +
                    "&token=" + tokenResult.getToken();
            platformQRoomInfos.add(new PlatformQRoomInfo(liveUserUserInfo.getDisplayName(),room.getOtherPlatformGameCode(PLATFORM_Q),room.getOtherPlatformRoomCode(PLATFORM_Q),url,avatarUrl));
        }
        return platformQRoomInfos;
    }

    public List<String> getQPlatformLines(){
       return domainConfigDaosService.findPlatformNormalUrl(PLATFORM_Q).stream()
                .map(config->"https://"+ H5+"."+config.getDomainName()).collect(Collectors.toList());
    }


}
