package com.donglai.model.db.entity.statistics;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.common.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import javax.persistence.Id;
import java.util.Set;

@Document
@Data
@NoArgsConstructor
public class DailyOfServerData {
    @AutoIncKey
    @Id
    private long id;
    /**
     * 新增用户
     */
    private long newUserNum;
    /**
     * 老用户(注册就算老用户,等于正式用户数量)
     */
    private long oldUserNum;
    /**
     * ip访问记录
     */
    @JsonIgnore
    private Set<String> ipHistory;
    /**
     * 总正式用户注册数
     */
    private long totalUserNum;
    /**
     * 登录用户数
     */
    private long loginUserNum;
    /**
     * 记录时间
     */
    @Indexed(unique = true)
    private long recordTime;

    public DailyOfServerData(long newUserNum, long oldUserNum, Set<String> ipHistory, long totalUserNum, long loginUserNum, long recordTime) {
        this.newUserNum = newUserNum;
        this.oldUserNum = oldUserNum;
        this.ipHistory = ipHistory;
        this.totalUserNum = totalUserNum;
        this.loginUserNum = loginUserNum;
        this.recordTime = recordTime;
    }

    public DailyOfServerData(long newUserNum, long oldUserNum, Set<String> ipHistory, long loginUserNum, long time) {
        this.newUserNum = newUserNum;
        this.oldUserNum = oldUserNum;
        this.ipHistory = ipHistory;
        this.recordTime = TimeUtil.getTimeDayStartTime(time);
    }

    public static DailyOfServerData newInstance(long newUserNum, long oldUserNum,Set<String> ipHistory,  long loginUserNum,long time) {
        return new DailyOfServerData(newUserNum, oldUserNum, ipHistory, loginUserNum, time);
    }

    public long getIpNum() {
        if (CollectionUtils.isEmpty(ipHistory)) return 0;
        return ipHistory.size();
    }
}
