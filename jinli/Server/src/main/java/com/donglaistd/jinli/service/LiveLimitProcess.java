package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.entity.*;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import com.donglaistd.jinli.http.dto.reply.LiveLimitListReply;
import com.donglaistd.jinli.http.dto.request.AddLiveLimitListRequest;
import com.donglaistd.jinli.http.dto.request.LiveLimitListRequest;
import com.donglaistd.jinli.http.dto.request.LiveLimitRecordRequest;
import com.donglaistd.jinli.http.dto.request.LiveTimeImportRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.GameConstant.*;
import static com.donglaistd.jinli.constant.QueueType.LIVE_LIMIT_AUTO_CLOSE;

@Component
public class LiveLimitProcess {
    @Value("${live.limit.interim.time}")
    public long liveDelayTime;

    @Autowired
    UserDaoService userDaoService;
    @Autowired
    LiveLimitListDaoService liveLimitListDaoService;
    @Autowired
    LiveLimitRecordDaoService liveLimitRecordDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    QueueProcess queueProcess;
    @Autowired
    QueueExecuteDaoService queueExecuteDaoService;
    @Autowired
    BackOfficeUserDaoService backOfficeUserDaoService;

    public boolean allowLiveNow(LiveUser liveUser){
        if(liveUser.isAddWhiteList()){
            return true;
        }else{
            LiveLimitList liveLimitList = liveLimitListDaoService.findByPlatformAndLimitDate(liveUser.getPlatformType(), System.currentTimeMillis());
            if(Objects.isNull(liveLimitList)) return false;
            long liveUserRecentLiveTime = liveLimitList.getLiveUserRecentLiveTime(liveUser.getId());
            if(liveUserRecentLiveTime<=0) return false;
            long delayTime = liveUserRecentLiveTime - System.currentTimeMillis();
            int liverHour = TimeUtil.getHourByTime(liveUserRecentLiveTime);
            int nowHour = TimeUtil.getHourByTime(System.currentTimeMillis());
            return liverHour == nowHour || (delayTime > 0 && delayTime <= liveDelayTime);
        }
    }

    //=====================update LiveLimitList========================

    public int uploadLiveLimitList(Map<Long, List<LiveTimeImportRequest>> limitListMap, Constant.PlatformType platform){
        Set<String> liveUserIds = new HashSet<>();
        limitListMap.values().forEach(requestList->requestList.forEach(request -> liveUserIds.addAll(request.getStrLiveUserIds())));
        long liveUserNum = liveUserDaoService.countPassLiveUserByIdsAndPlatform(liveUserIds, platform);
        if(liveUserNum != liveUserIds.size()) return LIVE_USER_NOT_EXIT;
        List<LiveLimitList> liveLimitLists = new ArrayList<>();
        limitListMap.forEach((key,liveTimeList)->{
            liveLimitListDaoService.deleteByPlatformAndLimitDate(platform, key);
            liveLimitLists.add(new LiveLimitList(key, platform, liveTimeList));
        });
        liveLimitListDaoService.saveAll(liveLimitLists);
        liveUserIds.forEach(this::updateLiveUserAutoEndLiveTask);
        return SUCCESS;
    }

    public boolean verifyUploadLiveLimitList(AddLiveLimitListRequest request){
        if (StringUtils.isNullOrBlank(request.getLiveUserId())) {
            return false;
        }
        if(request.isAddWhiteList()) return true;
        else if(request.getLiveEndHour() == 0 && Objects.equals(request.getLiveStartHour(),request.getLiveEndHour())){
            return true;
        }
        else return request.getLiveEndHour() <= (DAY_OF_MAX_HOUR +1) && request.getLiveStartHour() >= DAY_OF_MIN_HOUR
                && request.getLiveLimitDate() > 0
                && request.getLiveStartHour() <= request.getLiveEndHour();
    }

    public int uploadLiveLimitListByAddLiveLimitListRequest(AddLiveLimitListRequest request,String backofficeOfficeUerName) {
        LiveLimitList liveLimitList = Optional.ofNullable(liveLimitListDaoService.findByPlatformAndLimitDate(request.getPlatformType(), request.getLiveLimitDate()))
                .orElse(new LiveLimitList(request.getLiveLimitDate(), request.getPlatformType()));
        LiveUser liveUser = dataManager.findLiveUser(request.getLiveUserId());
        BackOfficeUser backOfficeUser = backOfficeUserDaoService.findByAccountName(backofficeOfficeUerName);
        if(liveUser == null || !Objects.equals(liveUser.getPlatformType(),request.getPlatformType()))
            return LIVE_USER_NOT_EXIT;
        else if (!verifyUploadLiveLimitList(request)) return PARAM_ERROR;
        else{
            liveLimitList.cleanLiveUserRecord(request.getLiveUserId());
            for (int hour = request.getLiveStartHour(); hour < request.getLiveEndHour(); hour++) {
                liveLimitList.addClockWhiteList(hour, request.getLiveUserId());
            }
            LiveLimitRecord liveLimitRecord = new LiveLimitRecord(request.getLiveUserId(), liveLimitList.getLimitDate(), request.getLiveStartHour(),
                    request.getLiveEndHour(), request.isAddWhiteList(), backOfficeUser.getId(), request.getPlatformType());
            liveLimitListDaoService.save(liveLimitList);
            liveLimitRecordDaoService.save(liveLimitRecord);
            liveUser.setAddWhiteList(request.isAddWhiteList());
            dataManager.saveLiveUser(liveUser);
            updateLiveUserAutoEndLiveTask(request.getLiveUserId());
            return SUCCESS;
        }
    }

    //=================update liveUser LiveLimitTask====================
    public void updateLiveUserAutoEndLiveTask(String liveUserId){
        //如果未开播，则不更新定时任务(1个主播可能有多个房间)
        LiveUser liveUser = dataManager.findLiveUser(liveUserId);
        Room onlineRoom = DataManager.findOnlineRoom(liveUser.getRoomId());
        if(onlineRoom==null || !onlineRoom.isLive()) return;
        //找出 该主播的所有限制时间列表，取得最近的一个开播限制时间戳
        long lastLiveUserLimitList = getLastLiveLimitTimeListByLiveUser(liveUserId, liveUser.getPlatformType());
        updateLiveUserLiveLimitQueue(liveUserId, lastLiveUserLimitList);
    }

    public void updateLiveUserLiveLimitQueue(String liveUserId,long endTime){
        queueExecuteDaoService.deleteByRefIdAndTriggerType(LIVE_LIMIT_AUTO_CLOSE.getValue(),liveUserId);
        queueProcess.addLiveLimitQueue(liveUserId, endTime);
    }

    //获取主播截止目前无间断的直播结束时间
    public long getLastLiveLimitTimeListByLiveUser(String liveUserId, Constant.PlatformType platform){
        var liveUserLimitList = liveLimitListDaoService.findLiveUserLimitList(liveUserId, platform);
        var liveLimitListMap = liveUserLimitList.stream().collect(Collectors.toMap(LiveLimitList::getLimitDate, liveLimitList -> liveLimitList));
        long lastEndLiveTime = 0;
        int nowHour = TimeUtil.getDayOfHour();
        LiveLimitList liveLimitList;
        for (int i = 0; i < liveUserLimitList.size(); i++) {
            long dayTime = TimeUtil.getAfterDayStartTime(i);
            liveLimitList = liveLimitListMap.get(dayTime);
            if(liveLimitList == null) break;
            for (int hour = nowHour; hour <= 23; hour++) {
                if(!liveLimitList.allowLiveByHour(hour, liveUserId))
                  break;
                lastEndLiveTime = liveLimitList.getLiveEndTimeStampByHour(hour);
            }
            nowHour = 0;
        }
        return lastEndLiveTime;
    }


    public PageInfo<LiveLimitRecord> findLiveLimitAddRecord(LiveLimitRecordRequest request) {
        PageInfo<LiveLimitRecord> pageInfo = liveLimitRecordDaoService.pageQueryLiveLimitRecord(request);
        User user;
        for (LiveLimitRecord liveLimitRecord : pageInfo.getContent()) {
            user = userDaoService.findByLiveUserId(liveLimitRecord.getLiveUserId());
            if(user != null) {
                liveLimitRecord.setDisplayName(user.getDisplayName());
                liveLimitRecord.setAccount(user.getId());
            }
        }
        return pageInfo;
    }

/*    public  List<Map<String, Object>> requestLiveLimitList(LiveLimitListRequest request) {
        LiveLimitList liveLimitList = liveLimitListDaoService.findByPlatformAndLimitDate(request.getPlatformType(), request.getLimitDate());
        if(liveLimitList==null) liveLimitList = new LiveLimitList(request.getLimitDate(),request.getPlatformType());
        List<Map<String, Object>> data = new ArrayList<>();
        liveLimitList.getClockWhiteList().forEach((hour,liveUserIds)-> {
            Map<String,Object> content =new HashMap<>();
            content.put("time", TimeUtil.formatHourToStrHourRange(hour));
            int i = 1;
            for (String liveUserId : liveUserIds) {
                content.put(String.format("liveUser%s",i), new LiveUserSummaryReply(userDaoService.findByLiveUserId(liveUserId)));
                i++;
            }
            data.add(content);
        });
        return data;
    }*/

    public LiveLimitListReply requestLiveLimitList(LiveLimitListRequest request) {
        LiveLimitList liveLimitList = liveLimitListDaoService.findByPlatformAndLimitDate(request.getPlatformType(), request.getLimitDate());
        if(liveLimitList == null) liveLimitList = new LiveLimitList(request.getLimitDate(),request.getPlatformType());
        Map<Integer, List<User>> clockWhiteListUserDetail = new LinkedHashMap<>();
        liveLimitList.getClockWhiteList().forEach((hour,liveUserIds)->{
            clockWhiteListUserDetail.put(hour, new ArrayList<>());
            for (String liveUserId : liveUserIds) {
                clockWhiteListUserDetail.get(hour).add(userDaoService.findByLiveUserId(liveUserId));
            }
        });
        return new LiveLimitListReply(liveLimitList.getLimitDate(), clockWhiteListUserDetail);
    }
}
