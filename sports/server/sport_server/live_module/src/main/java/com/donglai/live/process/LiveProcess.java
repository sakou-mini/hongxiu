package com.donglai.live.process;

import com.donglai.common.util.StringUtils;
import com.donglai.common.util.live.LiveStream;
import com.donglai.common.util.live.LiveStreamFactory;
import com.donglai.live.message.producer.Producer;
import com.donglai.live.util.LiveRedisUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveRecord;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.entity.sport.SportLiveSchedule;
import com.donglai.model.db.service.live.LiveRecordService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.model.db.service.sport.SportLiveScheduleService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.ProtoBufMapper;
import com.donglai.protocol.util.PbRefUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.donglai.live.constant.Constant.SPORT_EVENT_MAX_INTERVAL_HOUR;
import static com.donglai.protocol.Constant.ResultCode.*;

@Component
@Slf4j
public class LiveProcess {
    public final static Map<String, Room> onlineRoomMap = new ConcurrentHashMap<>();
    @Value("${room.rank.coefficient}")
    private int coefficient;
    @Value("${sport.event.live.delay.time}")
    private int sportLiveDelayTime;

    @Autowired
    Producer producer;
    @Autowired
    RoomService roomService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    LiveRecordService liveRecordService;
    @Autowired
    SportLiveScheduleService sportLiveScheduleService;

    public void saveOnlineRoom(Room room) {
        onlineRoomMap.put(room.getId(), room);
        roomService.save(room);
    }

    public void removeOnlineRoom(Room room) {
        onlineRoomMap.remove(room.getId());
        room.cleanLiveRoom();
        roomService.save(room);
    }

    public Room getOnlineRoomByLiveUserId(String liveUserId) {
        if (StringUtils.isNullOrBlank(liveUserId)) return null;
        LiveUser liveUser = liveUserService.findById(liveUserId);
        if (Objects.isNull(liveUser)) return null;
        if (!roomService.roomIsLive(liveUser.getRoomId())) return null;
        return roomService.findById(liveUser.getRoomId());
    }

    public Room getOnlineRoomById(String roomId) {
        if (!roomService.roomIsLive(roomId)) return null;
        return roomService.findById(roomId);
    }

    public void quitOldRoomIfPresent(String userId, String newRoomId) {
        String oldRoomId = LiveRedisUtil.getUserEnterRoomRecord(userId);
        if (!StringUtils.isNullOrBlank(oldRoomId) && !Objects.equals(oldRoomId, newRoomId)) {
            log.info("user has join other room, will clean other Room");
            var quitRoomRequest = Live.LiveOfAutoQuitRoomRequest.newBuilder().setRoomId(oldRoomId);
            var request = HongXiu.HongXiuMessageRequest.newBuilder().setLiveOfAutoQuitRoomRequest(quitRoomRequest);
            String topic = PbRefUtil.getSendTopic(ProtoBufMapper.MessageType.REQUEST_MSG, HongXiu.HongXiuMessageRequest.LIVEOFAUTOQUITROOMREQUEST_FIELD_NUMBER);
            producer.send(topic, userId, HongXiu.HongXiuMessageRequest.LIVEOFAUTOQUITROOMREQUEST_FIELD_NUMBER, request.build(), null);
        }
    }

    //close live
    public void endLive(Room room) {
        log.info("room is closed:{}", room);
        LiveUser liveUser = liveUserService.findById(room.getLiveUserId());
        liveUser.setStatus(Constant.LiveUserStatus.LIVE_OFFLINE);
        liveUserService.save(liveUser);
        broadcastLiveOfEndLiveBroadcastMessage(room);
        Set<String> audiences = room.getAudiences();
        audiences.stream().filter(audience -> Objects.equals(room.getId(), LiveRedisUtil.getUserEnterRoomRecord(audience)))
                .forEach(LiveRedisUtil::cleanEnterRoomRecord);
        roomService.removeLiveRoom(room.getId());
        saveAndTotalLiveRecord(room, liveUser.getUserId());
        room.cleanLiveRoom();
        roomService.save(room);
    }

    @Transactional
    public void saveAndTotalLiveRecord(Room room,String userId){
        LiveRecord newLiveRecord = LiveRecord.newInstance(room.getLiveStartTime(),  System.currentTimeMillis(), room.getLiveUserId(), userId, room.getId());
        if(!StringUtils.isNullOrBlank(room.getEventId())){
            String eventId = room.getEventId();
            //1.查找当前赛事的配置主播。如果不是当前主播（证明开播途中 赛事更换了主播），则不存储开播记录。
            List<SportLiveSchedule> eventConfigs = sportLiveScheduleService.findByEventId(eventId);
            //仅测试
            /*if(eventConfigs.stream().noneMatch(sportLiveSchedule -> Objects.equals( sportLiveSchedule.getUserId(),userId))) {
                //liveRecordService.save(newLiveRecord);
                return;
            }*/
            List<LiveRecord> liveRecords = liveRecordService.findByEventId(room.getEventId());
            //2.当前离线次数
            int offlineCount = liveRecords.stream().mapToInt(LiveRecord::getOfflineCount).sum();
            //3.删除赛事相关直播记录
            liveRecordService.deleteByEventId(eventId);
            newLiveRecord.setEventId(room.getEventId());
            //4.更新赛事开播记录
            if(!CollectionUtils.isEmpty(liveRecords)){
                newLiveRecord.setOfflineCount(offlineCount +1);
                long startTime = liveRecords.stream().mapToLong(LiveRecord::getStartTime).min().orElse(newLiveRecord.getStartTime());
                newLiveRecord.setStartTime(startTime);
            }
        }
        liveRecordService.save(newLiveRecord);
    }

    //TODO only for test
    public void cleanAllRobotRoom() {
        roomService.getAllLiveRoom().stream().map(id -> roomService.findById(id))
                .filter(Room::isRobotRoom)
                .forEach(this::endLive);
    }

    public void quitRoom(Room room, User user) {
        if (room.notContainsUser(user.getId())) {
            return;
        }
        room.removeAudience(user.getId());
        LiveRedisUtil.cleanEnterRoomRecord(user.getId());
        roomService.save(room);
        broadCastQuitRoomMessage(user, room);
    }

    //get live stream url
    public String getLiveStreamUrl(Room room, boolean isLiveUser) {
        String liveCode = room.getLiveCode();
        log.info("line is----------->:{}", room.getLiveLineCode());
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(room.getLiveLineCode());
        if (Objects.isNull(liveStream)) {
            log.warn("not have LiveLine:{}", room.getLiveLineCode());
            return "";
        }
        return isLiveUser ? liveStream.getRtmpPushUrl(room.getLiveDomain(), liveCode) : liveStream.getRtmpPullUrl(room.getLiveDomain(), liveCode);
    }

    public static boolean VerifySwitchLiveSourceOrStartLiveParam(String liveDomain, int liveLineCode) {
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(liveLineCode);
        return !StringUtils.isNullOrBlank(liveDomain) && liveStream != null;
    }



    public Constant.ResultCode verifySportEventLive(SportLiveSchedule sportLiveSchedule){
        if(Objects.isNull(sportLiveSchedule)) return SPORT_EVENT_NOT_EXIT;
        long now = System.currentTimeMillis();
        long liveBeginTime = sportLiveSchedule.getLiveBeginTime();
        long intervalHour = TimeUnit.MILLISECONDS.toHours(Math.abs(now - liveBeginTime));
        long allowLiveStartTime = liveBeginTime - sportLiveDelayTime;
        //如果当前时间 >= allowLiveStartTime  并且当前时间和赛事开始时间不超过5小时都可以开播
        return now >= liveBeginTime && intervalHour <= SPORT_EVENT_MAX_INTERVAL_HOUR ? SUCCESS : SPORT_EVENT_OVERDUE;
    }

    //=============================================broadCastMessage======================================
    public void broadCastEnterLiveRoomMessage(User user, Room room) {
        var userInfo = user.toDetailProto().toBuilder().clearCoin().build();
        var message = Live.LiveOfEnterRoomBroadcastMessage.newBuilder().setUserInfo(userInfo)
                .setHotValue(room.getAudiences().size() * coefficient)
                .setRoomId(room.getId()).build();
        RoomProcess.broadCastMessage(room, message);
    }

    public void broadCastQuitRoomMessage(User user, Room room) {
        var message = Live.LiveOfQuitRoomBroadcastMessage.newBuilder().setUserInfo(user.toDetailProto().toBuilder().clearCoin().build())
                .setRoomId(room.getId()).setHotValue(room.getAudiences().size() * coefficient).build();
        RoomProcess.broadCastMessage(room, message);
    }

    public void broadcastLiveOfReadyEndLiveBroadcastMessage(Room room, long delayTime) {
        var message = Live.LiveOfEndLiveReadyBroadcastMessage.newBuilder().setTimeToEndLive(delayTime).build();
        RoomProcess.broadCastMessage(room, message);
    }

    public void broadcastLiveOfEndLiveBroadcastMessage(Room room) {
        var message = Live.LiveOfEndLiveBroadcastMessage.newBuilder().setRoomId(room.getId()).build();
        RoomProcess.broadCastMessage(room, message);
    }

    public void broadCastLiveOfSwitchLivePattern(Room room, String pushStreamUrl, String pullStreamUrl) {
        var message = Live.LiveOfSwitchLivePatternBroadcastMessage.newBuilder().setLivePushStreamUrl(pushStreamUrl)
                .setLivePullStreamUrl(pullStreamUrl).setPattern(room.getPattern());
        RoomProcess.broadCastMessage(room, message.build());
    }
}
