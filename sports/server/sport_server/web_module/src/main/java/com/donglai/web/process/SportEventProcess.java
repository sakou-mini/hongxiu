package com.donglai.web.process;

import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.entity.sport.SportEvent;
import com.donglai.model.db.entity.sport.SportLiveSchedule;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.model.db.service.sport.SportEventService;
import com.donglai.model.db.service.sport.SportLiveScheduleService;
import com.donglai.web.db.server.service.LiveRecordDbService;
import com.donglai.web.db.server.service.SportEventQueryService;
import com.donglai.web.response.GlobalResponseCode;
import com.donglai.web.response.PageInfo;
import com.donglai.web.util.OrderIdUtils;
import com.donglai.web.web.dto.reply.SportEventInfoReply;
import com.donglai.web.web.dto.reply.SportEventRecordReply;
import com.donglai.web.web.dto.request.HistoryEventRequest;
import com.donglai.web.web.dto.request.SettingEventLiveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.donglai.web.constant.WebConstant.EVENT_OVER_HOUR;
import static com.donglai.web.response.GlobalResponseCode.*;

@Component
public class SportEventProcess {
    @Autowired
    SportEventQueryService sportEventQueryService;
    @Autowired
    SportEventService sportEventService;
    @Autowired
    SportLiveScheduleService sportLiveScheduleService;
    @Autowired
    UserService userService;
    @Autowired
    RoomService roomService;
    @Autowired
    LiveRecordDbService liveRecordDbService;
    @Autowired
    LiveUserService liveUserService;

    /*1.查询当前赛事直播记录*/
    public PageInfo<SportEventInfoReply> queryLocalEvent(int page, int size){
        PageInfo<SportEvent> eventResult = sportEventQueryService.queryActiveSportEvent(PageRequest.of(page - 1, size));
        List<SportEventInfoReply> replyList = eventResult.getContent().stream().map(this::buildSportEventInfoReply).collect(Collectors.toList());
        return new PageInfo<>(eventResult.getPageNum(),eventResult.getPageSize(),eventResult.getTotal(),replyList);
    }

    private SportEventInfoReply buildSportEventInfoReply(SportEvent event){
        List<SportLiveSchedule> sportLiveScheduses = sportLiveScheduleService.findByEventId(event.getId());
        User user;
        if(!CollectionUtils.isEmpty(sportLiveScheduses)){
            //Todo 目前 直播和赛事是1 ： 1对应 ，以后可能改以1对 n
            SportLiveSchedule sportLiveSchedule = sportLiveScheduses.get(0);
            user = userService.findById(sportLiveSchedule.getUserId());
            if(Objects.nonNull(user)) {
                LiveUser liveUser = liveUserService.findById(user.getLiveUserId());
                SportEventInfoReply.EventLiveInfo eventLiveInfo = new SportEventInfoReply.EventLiveInfo(user, roomService.userIsLive(user.getId()),liveUser.getRoomId());
                eventLiveInfo.setLiveBeginTime(sportLiveSchedule.getLiveBeginTime());
                return new SportEventInfoReply(event, eventLiveInfo);
            }
        }
        return new SportEventInfoReply(event);
    }

    /*2.查询赛事直播历史记录*/
    public PageInfo<SportEventRecordReply> getEventRecord(HistoryEventRequest request){
        //查询过期赛事
        PageInfo<SportEvent> overSportEvent = sportEventQueryService.getOverSportEvent(request);
        List<SportEventRecordReply> recordReplyList = overSportEvent.getContent().stream().map(this::buildSportEventRecordReply).collect(Collectors.toList());
        return new PageInfo<>(overSportEvent.getPageNum(),overSportEvent.getPageSize(),overSportEvent.getTotal(),recordReplyList);
    }

    private SportEventRecordReply buildSportEventRecordReply(SportEvent event){
        var sportLiveSchedules = sportLiveScheduleService.findByEventId(event.getId());
        SportEventRecordReply.EventLiveRecord eventLiveRecord = null;
        if(!CollectionUtils.isEmpty(sportLiveSchedules)){
            //目前只支持1个赛事 1个主播
            SportLiveSchedule sportLiveSchedule = sportLiveSchedules.get(0);
            User user = userService.findById(sportLiveSchedule.getUserId());
            if (Objects.nonNull(user)) eventLiveRecord = new SportEventRecordReply.EventLiveRecord(user);
        }
        return new SportEventRecordReply(event, eventLiveRecord);
    }



    public SportEvent mockEvent(String name, long time, String homeTeam, String awayTeam, String competition, String sport) {
        SportEvent sportEvent = new SportEvent();
        sportEvent.setId(OrderIdUtils.getOrderNumber());
        sportEvent.setEventName(name);
        sportEvent.setEventDatetime(time);
        sportEvent.setEventHomeTeam(homeTeam);
        sportEvent.setEventAwayTeam(awayTeam);
        sportEvent.setEventCompetition(competition);
        sportEvent.setEventSport(sport);
        return sportEventService.save(sportEvent);
    }

    //3.设置 赛事直播
    public GlobalResponseCode settingEventLive(SettingEventLiveRequest request) {
        if(StringUtils.isNullOrBlank(request.eventId) ||
                (!StringUtils.isNullOrBlank(request.getUserId()) && Objects.isNull(request.getLiveBeginTime())) )
            return PARAM_ERROR;
        SportEvent sportEvent = sportEventService.findByEventId(request.eventId);
        if(Objects.isNull(sportEvent)) return EVENT_NOT_EXIT;
        else if(eventIsOver(sportEvent)) return EVENT_OVER;
        if(!StringUtils.isNullOrBlank(request.getUserId())) {
            LiveUser liveUser = liveUserService.findByUserId(request.getUserId());
            if(Objects.isNull(liveUser)) return LIVE_USER_NOT_EXIT;
            //清除掉与此赛事相关的主播  以及已开播的主播
            cleanSportLiveSchedulingAndLive(sportEvent.getId(), request.userId);
            //添加新的直播安排（就算该主播已经安排了其他赛事，也不影响他当前的直播间）
            SportLiveSchedule sportLiveSchedule = SportLiveSchedule.newInstance(request.getEventId(), liveUser, request.getLiveBeginTime());
            sportLiveScheduleService.save(sportLiveSchedule);
        }else {
            //清空赛事相关直播
            cleanSportLiveSchedulingAndLive(request.getEventId(), "");
        }
        return SUCCESS;
    }


    public static boolean eventIsOver(SportEvent sportEvent){
        long raceOverTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(EVENT_OVER_HOUR);
        return sportEvent.getEventDatetime() < raceOverTime;
    }

    /*清除掉与此赛事相关的主播  以及已开播的主播*/
    private void cleanSportLiveSchedulingAndLive(String eventId,String newLiveUserId){
        List<SportLiveSchedule> sportLiveSchedules = sportLiveScheduleService.findByEventId(eventId);
        LiveUser liveUser;
        Room room;
        for (SportLiveSchedule sportLiveSchedule : sportLiveSchedules) {
            liveUser = liveUserService.findById(sportLiveSchedule.getLiveUserId());
            if(Objects.nonNull(liveUser) && !Objects.equals(newLiveUserId,sportLiveSchedule.getLiveUserId()) && roomService.roomIsLive(liveUser.getRoomId())){
                //置空直播间赛事信息
                room = roomService.findById(liveUser.getRoomId());
                //TODO 是否结束直播(广播通知直播结束)，或者不通知结束。只清除相关的赛事信息
                room.cleanEventInfo();
                roomService.save(room);
            }
            //删除相关 SportLiveSchedule
            sportLiveScheduleService.del(sportLiveSchedule);
        }
    }
}
