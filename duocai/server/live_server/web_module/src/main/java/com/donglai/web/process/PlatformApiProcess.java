package com.donglai.web.process;

import com.donglai.common.util.PasswordUtil;
import com.donglai.common.util.StringUtils;
import com.donglai.common.util.live.LiveStream;
import com.donglai.common.util.live.LiveStreamFactory;
import com.donglai.model.db.entity.common.H5DomainConfig;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.entity.other.PlatformToken;
import com.donglai.model.db.service.common.H5DomainConfigService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Constant;
import com.donglai.web.builder.UserBuilder;
import com.donglai.web.constant.WebPathConstant;
import com.donglai.web.response.ErrorResponse;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.util.DuoCaiPasswordUtil;
import com.donglai.web.web.dto.reply.PlatformRoomListReply;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.donglai.common.constant.PathConstant.DEFAULT_AVATAR_PATH;
import static com.donglai.common.constant.RedisConstant.getPlatformTokenKey;
import static com.donglai.web.response.GlobalResponseCode.*;

@Component
public class PlatformApiProcess {
    @Value("${token.expire.time.milliseconds}")
    public long tokenExpireTimeMilliseconds;
    @Autowired
    UserService userService;
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    RoomService roomService;
    @Autowired
    LiveUserProcess liveUserProcess;
    @Autowired
    H5DomainConfigService h5DomainConfigService;

    //=======================request token =======================

    public PlatformToken generatedToken(String ip) {
        return saveRequestTokenResult(ip);
    }

    private PlatformToken saveRequestTokenResult(String ip) {
        long expireTime = System.currentTimeMillis() + tokenExpireTimeMilliseconds;
        PlatformToken tokenResult = new PlatformToken(UUID.randomUUID().toString(), expireTime, ip);
        String tokenKey = getPlatformTokenKey(tokenResult.getToken());
        redisTemplate.opsForValue().set(tokenKey, tokenResult, tokenExpireTimeMilliseconds, TimeUnit.MILLISECONDS);
        return tokenResult;
    }

    public PlatformToken getTokenResultByToken(String token) {
        String tokenKey = getPlatformTokenKey(token);
        return (PlatformToken) redisTemplate.opsForValue().get(tokenKey);
    }

    public PlatformRoomListReply getPlatformRoomListReply(PlatformToken tokenResult) {
        List<PlatformRoomListReply.LiveRoomInfo> roomReplyList = new ArrayList<>();
        Set<String> roomIds = roomService.getAllLiveRoom();
        Room room;
        String url;
        if (roomIds != null) {
            for (String roomId : roomIds) {
                room = roomService.findById(roomId);
                if (room != null && !room.isClose()) {
                    url = WebPathConstant.getPlatformLiveRoomUrl(room.getId(), tokenResult.getToken());
                    roomReplyList.add(new PlatformRoomListReply.LiveRoomInfo(userService.findById(room.getUserId()), room, url));
                }
            }
        }
        List<String> domains = h5DomainConfigService.findAll().stream().filter(H5DomainConfig::isStatus).map(H5DomainConfig::getDomainName).collect(Collectors.toList());
        //todo ,if has many domains ,there setting
        return new PlatformRoomListReply(domains, roomReplyList);
    }

    /*============getLiveUrl==================*/
    public RestResponse getLiveRoomLiveStream(String requestIp, String roomId, String token) {
        if (StringUtils.isNullOrBlank(roomId) || StringUtils.isNullOrBlank(token))
            return new ErrorResponse(MISSING_PARAMETER);
        PlatformToken platformToken = getTokenResultByToken(token);
        if (Objects.isNull(platformToken))
            return new ErrorResponse(TOKEN_EXPIRE);
        if (!Objects.equals(requestIp, platformToken.getCreatedIp())) {
            return new ErrorResponse(TOKEN_INVALID);
        }
        Room room = roomService.findById(roomId);
        if (Objects.isNull(room) || !room.isLive()) {
            return new ErrorResponse(ROOM_NOT_LIVE);
        }
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(room.getLiveLineCode());
        assert liveStream != null;
        String m3u8Url = liveStream.getFullHttpLiveUrlByType(room.getLiveDomain(), room.getLiveCode(), LiveStreamFactory.LiveStreamUrlType.m3u8);
        String flvUrl = liveStream.getFullHttpLiveUrlByType(room.getLiveDomain(), room.getLiveCode(), LiveStreamFactory.LiveStreamUrlType.flv);
        String rtmpPushUrl = liveStream.getRtmpPushUrl(room.getLiveDomain(), room.getLiveCode());
        System.out.println("推流地址:" + rtmpPushUrl);
        Map<String, Object> liveUrlMap = new HashMap<>();
        liveUrlMap.put(LiveStreamFactory.LiveStreamUrlType.m3u8.name(), m3u8Url);
        liveUrlMap.put(LiveStreamFactory.LiveStreamUrlType.flv.name(), flvUrl);
        liveUrlMap.put("livePattern", room.getPattern().getNumber());
        return new SuccessResponse().withData(liveUrlMap);
    }

    public User initDuoCaiUserAndBecomeLiveUser(String account) {
        Constant.PlatformType platform = Constant.PlatformType.DUOCAI;
        User user = userService.findByAccountId(account);
        if (user == null) {
            String password = DuoCaiPasswordUtil.generatedPwd();
            user = userBuilder.createUser(account, PasswordUtil.encodePassword(password), DEFAULT_AVATAR_PATH, platform);
        }
        if (StringUtils.isNullOrBlank(user.getLiveUserId())) liveUserProcess.becomeSimpleLiveUser(user, platform);
        return user;
    }
}
