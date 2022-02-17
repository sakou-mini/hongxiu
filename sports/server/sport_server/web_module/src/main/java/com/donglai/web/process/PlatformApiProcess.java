package com.donglai.web.process;

import com.donglai.common.util.StringUtils;
import com.donglai.common.util.live.LiveStream;
import com.donglai.common.util.live.LiveStreamFactory;
import com.donglai.model.db.entity.common.H5DomainConfig;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.entity.other.PlatformToken;
import com.donglai.model.db.entity.sport.SportLiveSchedule;
import com.donglai.model.db.service.common.H5DomainConfigService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.model.db.service.sport.SportLiveScheduleService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.ProtoBufMapper;
import com.donglai.protocol.message.KafkaMessage;
import com.donglai.protocol.util.PbRefUtil;
import com.donglai.web.builder.UserBuilder;
import com.donglai.web.constant.WebPathConstant;
import com.donglai.web.message.producer.Producer;
import com.donglai.web.response.ErrorResponse;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.web.dto.reply.SportLiveReply;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.donglai.common.constant.RedisConstant.getPlatformTokenKey;
import static com.donglai.protocol.util.PbRefUtil.buildRequest;
import static com.donglai.web.constant.WebConstant.SPORT_AUTH_KEY;
import static com.donglai.web.response.GlobalResponseCode.*;

@Component
@Slf4j
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
    LiveUserService liveUserService;
    @Autowired
    SportLiveScheduleService sportLiveScheduleService;
    @Autowired
    Producer producer;
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
        if(Objects.isNull(liveStream)) {
            log.error("直播间未找到对应的直播线路");
            return new ErrorResponse(ROOM_NOT_LIVE);
        }
        String m3u8Url = liveStream.getFullHttpLiveUrlByType(room.getLiveDomain(), room.getLiveCode(), LiveStreamFactory.LiveStreamUrlType.m3u8);
        String flvUrl = liveStream.getFullHttpLiveUrlByType(room.getLiveDomain(), room.getLiveCode(), LiveStreamFactory.LiveStreamUrlType.flv);
        String rtmpPushUrl = liveStream.getRtmpPushUrl(room.getLiveDomain(), room.getLiveCode());
        log.info("推流地址:" + rtmpPushUrl);
        Map<String, Object> liveUrlMap = new HashMap<>();
        liveUrlMap.put(LiveStreamFactory.LiveStreamUrlType.m3u8.name(), m3u8Url);
        liveUrlMap.put(LiveStreamFactory.LiveStreamUrlType.flv.name(), flvUrl);
        liveUrlMap.put("livePattern", room.getPattern().getNumber());
        return new SuccessResponse().withData(liveUrlMap);
    }

    //====================Sport=============================
    private SportLiveReply buildSportLiveReplyBySportLiveSchedulingList(List<SportLiveSchedule> sportLiveScheduleList, String token){
        List<SportLiveReply.SportLiveInfo> sportLiveReplies = new ArrayList<>();
        SportLiveReply.SportLiveInfo sportLiveInfo;
        for (SportLiveSchedule sportLiveSchedule : sportLiveScheduleList) {
            sportLiveInfo = buildSportLiveReply(sportLiveSchedule, token);
            if(Objects.nonNull(sportLiveInfo)) sportLiveReplies.add(sportLiveInfo);
        }
        List<String> domains = h5DomainConfigService.findAll().stream().filter(H5DomainConfig::isStatus).map(H5DomainConfig::getDomainName).collect(Collectors.toList());
        return new SportLiveReply(domains,sportLiveReplies);
    }

    private SportLiveReply.SportLiveInfo buildSportLiveReply(SportLiveSchedule sportLiveSchedule, String token){
        User user = userService.findById(sportLiveSchedule.getUserId());
        Room room = roomService.findByLiveUserId(sportLiveSchedule.getLiveUserId());
        if(Objects.nonNull(user) && Objects.nonNull(room)){
            boolean live = roomService.roomIsLive(room.getId());
            SportLiveReply.SportLiveInfo sportLiveReply = new SportLiveReply.SportLiveInfo(user, room, live, sportLiveSchedule.getEventId());
            if(live){
                String roomUrl = WebPathConstant.getSportLiveRoomUrl(room.getId(), token);
                sportLiveReply.setRoomUrl(roomUrl);
            }
            return sportLiveReply;
        }
        return null;
    }
    //根据赛事id获取直播
    public SportLiveReply queryLiveEventByEventId(String eventId, String ip){
        PlatformToken platformToken = generatedToken(ip);
        /*var liveSchedulingList = sportLiveScheduleService.findByEventId(eventId);
        return buildSportLiveReplyBySportLiveSchedulingList(liveSchedulingList,platformToken.getToken());*/

        //TODO  查询当前的直播，随机获取一个 (仅临时测试)
        Set<String> liveRoom = roomService.getAllLiveRoom();
        if(!CollectionUtils.isEmpty(liveRoom)){
            String roomId = Lists.newArrayList(liveRoom).get(0);
            SportLiveSchedule sportLiveSchedule = livingRoomToSportLiveScheduling(roomId);
            sportLiveSchedule.setEventId(eventId);
            return buildSportLiveReplyBySportLiveSchedulingList(Lists.newArrayList(sportLiveSchedule), platformToken.getToken());
        }else {
            List<String> domains = h5DomainConfigService.findAll().stream().filter(H5DomainConfig::isStatus).map(H5DomainConfig::getDomainName).collect(Collectors.toList());
            return new SportLiveReply(domains, null);
        }
    }

    //FIXME ONLY IN TEST (DELETE)
    public SportLiveSchedule livingRoomToSportLiveScheduling(String roomId){
        Room room = roomService.findById(roomId);
        User user = userService.findById(room.getUserId());
        LiveUser liveUser = liveUserService.findById(user.getLiveUserId());
        return SportLiveSchedule.newInstance(room.getEventId(), liveUser, room.getLiveStartTime());
    }

    //查询近期安排的所有赛事直播
    public SportLiveReply getAllEventLive(String ip) {
        PlatformToken platformToken = generatedToken(ip);
        /*List<SportLiveSchedule> sportLiveSchedulingList = sportLiveScheduleService.findAllSportLiveAfterToday();
        return buildSportLiveReplyBySportLiveSchedulingList(sportLiveSchedulingList,platformToken.getToken());*/
        //TODO DeL , 仅测试直播间
        Set<String> liveRoom = roomService.getAllLiveRoom();
        List<SportLiveSchedule> schedulingList = Optional.ofNullable(liveRoom).orElse(new HashSet<>()).stream().map(this::livingRoomToSportLiveScheduling).collect(Collectors.toList());
        return buildSportLiveReplyBySportLiveSchedulingList(schedulingList, platformToken.getToken());
    }

    //通知主播赛事结束
    public boolean raceOver(String liveUserId, int giftIncome, int popularity) {
        if(StringUtils.isNullOrBlank(liveUserId)) return false;
        // kafak 发送消息给主播
        User user = userService.findByLiveUserId(liveUserId);
        if(user==null) return false;
        KafkaMessage.TopicMessage topicMessage = buildSportLiveOverRequest(user.getId(), liveUserId, giftIncome, popularity);
        producer.send(topicMessage);
        //查询主播对应的赛事
        return true;
    }

    public KafkaMessage.TopicMessage buildSportLiveOverRequest(String userId , String liveUserId, int giftIncome, int popularity){
        var builder = Live.LiveOfSportLiveOverRequest.newBuilder().setLiveUserId(liveUserId).setGiftIncome(giftIncome)
                .setPopularity(popularity);
        HongXiu.HongXiuMessageRequest request = buildRequest(builder.build());
        int messageId = HongXiu.HongXiuMessageRequest.LIVEOFSPORTLIVEOVERREQUEST_FIELD_NUMBER;
        String topic = PbRefUtil.getSendTopic(ProtoBufMapper.MessageType.REQUEST_MSG, messageId);
        return new KafkaMessage.TopicMessage(Constant.PlatformType.SPORT, topic, userId, messageId, request);
    }

    //校验请求头
    public static boolean verifyHeaderToken(HttpServletRequest request){
        String header = request.getHeader("Auth-Key");
        return Objects.equals(SPORT_AUTH_KEY, header);
    }
}
