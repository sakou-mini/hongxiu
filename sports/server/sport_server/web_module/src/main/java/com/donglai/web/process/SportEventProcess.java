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

    /*1.??????????????????????????????*/
    public PageInfo<SportEventInfoReply> queryLocalEvent(int page, int size){
        PageInfo<SportEvent> eventResult = sportEventQueryService.queryActiveSportEvent(PageRequest.of(page - 1, size));
        List<SportEventInfoReply> replyList = eventResult.getContent().stream().map(this::buildSportEventInfoReply).collect(Collectors.toList());
        return new PageInfo<>(eventResult.getPageNum(),eventResult.getPageSize(),eventResult.getTotal(),replyList);
    }

    private SportEventInfoReply buildSportEventInfoReply(SportEvent event){
        List<SportLiveSchedule> sportLiveScheduses = sportLiveScheduleService.findByEventId(event.getId());
        User user;
        if(!CollectionUtils.isEmpty(sportLiveScheduses)){
            //Todo ?????? ??????????????????1 ??? 1?????? ?????????????????????1??? n
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

    /*2.??????????????????????????????*/
    public PageInfo<SportEventRecordReply> getEventRecord(HistoryEventRequest request){
        //??????????????????
        PageInfo<SportEvent> overSportEvent = sportEventQueryService.getOverSportEvent(request);
        List<SportEventRecordReply> recordReplyList = overSportEvent.getContent().stream().map(this::buildSportEventRecordReply).collect(Collectors.toList());
        return new PageInfo<>(overSportEvent.getPageNum(),overSportEvent.getPageSize(),overSportEvent.getTotal(),recordReplyList);
    }

    private SportEventRecordReply buildSportEventRecordReply(SportEvent event){
        var sportLiveSchedules = sportLiveScheduleService.findByEventId(event.getId());
        SportEventRecordReply.EventLiveRecord eventLiveRecord = null;
        if(!CollectionUtils.isEmpty(sportLiveSchedules)){
            //???????????????1????????? 1?????????
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

    //3.?????? ????????????
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
            //????????????????????????????????????  ????????????????????????
            cleanSportLiveSchedulingAndLive(sportEvent.getId(), request.userId);
            //????????????????????????????????????????????????????????????????????????????????????????????????????????????
            SportLiveSchedule sportLiveSchedule = SportLiveSchedule.newInstance(request.getEventId(), liveUser, request.getLiveBeginTime());
            sportLiveScheduleService.save(sportLiveSchedule);
        }else {
            //????????????????????????
            cleanSportLiveSchedulingAndLive(request.getEventId(), "");
        }
        return SUCCESS;
    }


    public static boolean eventIsOver(SportEvent sportEvent){
        long raceOverTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(EVENT_OVER_HOUR);
        return sportEvent.getEventDatetime() < raceOverTime;
    }

    /*????????????????????????????????????  ????????????????????????*/
    private void cleanSportLiveSchedulingAndLive(String eventId,String newLiveUserId){
        List<SportLiveSchedule> sportLiveSchedules = sportLiveScheduleService.findByEventId(eventId);
        LiveUser liveUser;
        Room room;
        for (SportLiveSchedule sportLiveSchedule : sportLiveSchedules) {
            liveUser = liveUserService.findById(sportLiveSchedule.getLiveUserId());
            if(Objects.nonNull(liveUser) && !Objects.equals(newLiveUserId,sportLiveSchedule.getLiveUserId()) && roomService.roomIsLive(liveUser.getRoomId())){
                //???????????????????????????
                room = roomService.findById(liveUser.getRoomId());
                //TODO ??????????????????(????????????????????????)?????????????????????????????????????????????????????????
                room.cleanEventInfo();
                roomService.save(room);
            }
            //???????????? SportLiveSchedule
            sportLiveScheduleService.del(sportLiveSchedule);
        }
    }
}
