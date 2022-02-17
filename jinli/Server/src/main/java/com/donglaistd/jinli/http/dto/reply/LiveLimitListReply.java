package com.donglaistd.jinli.http.dto.reply;

import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LiveLimitListReply {
    private long limitDate;
    private int maxLiveUserNum;
    private List<LiveHourData> data = new ArrayList<>();


    public LiveLimitListReply(long limitDate, Map<Integer,List<User>> users){
        this.limitDate = limitDate;
        users.forEach((k,v)->{
            maxLiveUserNum = Math.max(maxLiveUserNum, v.size());
            addUserToLiveList(k, v);
        });
    }

    public void addUserToLiveList(int hour,List<User> users) {
        data.add(new LiveHourData(TimeUtil.formatHourToStrHourRange(hour),users));
    }

    public long getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(long limitDate) {
        this.limitDate = limitDate;
    }

    public int getMaxLiveUserNum() {
        return maxLiveUserNum;
    }

    public void setMaxLiveUserNum(int maxLiveUserNum) {
        this.maxLiveUserNum = maxLiveUserNum;
    }

    public List<LiveHourData> getData() {
        return data;
    }

    public void setData(List<LiveHourData> data) {
        this.data = data;
    }

    public static class LiveHourData{
        public String time;
        public List<LiveUserInfo> userInfos = new ArrayList<>();

        public LiveHourData(String time, List<User> users) {
            this.time = time;
            for (User user : users) {
                userInfos.add(new LiveUserInfo(user));
            }
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public List<LiveUserInfo> getUserInfos() {
            return userInfos;
        }

        public void setUserInfos(List<LiveUserInfo> userInfos) {
            this.userInfos = userInfos;
        }
    }

    public static class LiveUserInfo{
        public String displayName;
        public String liveUserId;
        public LiveUserInfo(User user) {
            this.displayName = user.getDisplayName();
            this.liveUserId = user.getLiveUserId();
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getLiveUserId() {
            return liveUserId;
        }

        public void setLiveUserId(String liveUserId) {
            this.liveUserId = liveUserId;
        }
    }
}
