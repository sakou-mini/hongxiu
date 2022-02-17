package com.donglaistd.jinli.http.dto.request;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.donglaistd.jinli.util.TimeUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ExcelTarget(value = "liveLimit")
public class LiveTimeImportRequest implements Serializable {
    @Excel(name = "时间段")
    public String hourRange;

    @ExcelCollection(name = "开播白名单")
    public List<LiveUserId> liveUserIds = new ArrayList<>();

    public String getHourRange() {
        return hourRange;
    }

    public void setHourRange(String hourRange) {
        this.hourRange = hourRange;
    }

    public List<LiveUserId> getLiveUserIds() {
        return liveUserIds.stream().filter(userId -> Objects.nonNull(userId.getLiveUser())).collect(Collectors.toList());
    }

    public void setLiveUserIds(List<LiveUserId> liveUserIds) {
        this.liveUserIds = liveUserIds;
    }
    public List<String> getStrLiveUserIds() {
        return liveUserIds.stream().filter(userId -> Objects.nonNull(userId.getLiveUser())).map(LiveUserId::getLiveUser).collect(Collectors.toList());
    }

    public int getLimitStartHour(){
        String[] split = hourRange.split("-");
        if(split.length <= 0) return -1;
        return TimeUtil.strHourToNumber(split[0]);
    }

    public static class LiveUserId {
        @Excel(name = "主播id")
        public String liveUser;

        public String getLiveUser() {
            return liveUser;
        }

        public void setLiveUser(String liveUser) {
            this.liveUser = liveUser;
        }
    }

}
