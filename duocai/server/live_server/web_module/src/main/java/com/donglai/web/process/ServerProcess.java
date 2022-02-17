/*
//FIXME 暂时保留，此类用于操作服务器
package com.donglai.web.process;

import com.donglai.common.constant.ServerStatus;
import com.donglai.common.util.TimeUtil;
import com.donglai.model.db.entity.common.ServerProperty;
import com.donglai.model.db.entity.statistics.DailyOfServerData;
import com.donglai.model.db.service.common.ServerPropertyService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.model.db.service.statistics.DailyOfServerDataService;
import com.donglai.web.db.server.service.UserQueryDbService;
import com.donglai.web.web.dto.reply.ServerInfoReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Component
public class ServerProcess {
    @Autowired
    UserQueryDbService userQueryDbService;
    @Autowired
    RoomService roomService;
    @Autowired
    ServerPropertyService serverPropertyService;
    @Autowired
    DailyOfServerDataService dailyOfServerDataService;

    public ServerInfoReply getServerInfo() {
        long onlineUserNum = userQueryDbService.countOnlineUserNum();
        long liveRoomNum = roomService.getAllLiveRoom().size();
        ServerProperty serverProperty =serverPropertyService.getServerProperty();
        //每日访问量（目前统计为每日登录数量）
        int monthBeforeDay = 30;
        long monthStartTime = TimeUtil.getBeforeDayStartTime(monthBeforeDay);
        List<DailyOfServerData> dailyData = dailyOfServerDataService.findByTimeBetween(monthStartTime, System.currentTimeMillis());
        Map<Long, Long> monthVisitMap = buildMonthVisitMap(dailyData, monthBeforeDay);
        return ServerInfoReply.newInstance(serverProperty.getServerStatus(), onlineUserNum, liveRoomNum, monthVisitMap);
    }

    private Map<Long, Long> buildMonthVisitMap(List<DailyOfServerData> dailyData,int beforeDay) {
        Map<Long, Long> dailyVisitMap = dailyData.stream().collect(Collectors.toMap(DailyOfServerData::getRecordTime, DailyOfServerData::getLoginUserNum));
        Map<Long, Long> monthVisitMap = new LinkedHashMap<>();
        long beforeDayStartTime;
        for (int i = beforeDay; i > 0; i--) {
            beforeDayStartTime = TimeUtil.getBeforeDayStartTime(i);
            monthVisitMap.put(TimeUtil.getBeforeDayStartTime(i), dailyVisitMap.getOrDefault(beforeDayStartTime, 0L));
        }
        return monthVisitMap;
    }


    public void closeOrOpenServer(boolean open) {
        ServerProperty serverProperty =serverPropertyService.getServerProperty();
        serverProperty.setServerStatus(ServerStatus.RUNNING);
    }
}
*/
