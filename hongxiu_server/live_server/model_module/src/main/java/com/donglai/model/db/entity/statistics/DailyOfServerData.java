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
import java.util.Date;
import java.util.Set;

@Document
@Data
@NoArgsConstructor
public class DailyOfServerData {
    @AutoIncKey
    @Id
    private long id;
    /**
     * 新增游客
     */
    private long newTouristNum;
    /**
     * 新增正式用户
     */
    private long newUserNum;
    /**
     * 老用户(注册就算老用户,等于正式用户数量)
     */
    private long oldUserNum;
    /**
     * 新增点赞数
     */
    private long newLikeNum;
    /**
     * 新增评论
     */
    private long newCommentNum;
    /**
     * 新增动态
     */
    private long newBlogsNum;
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
     * 总游客数
     */
    private long totalTouristNum;
    /**
     * 总动态数
     */
    private long totalBlogsNum;
    /**
     * 总评论数
     */
    private long totalCommentNum;
    /**
     * 记录时间
     */
    @Indexed(unique = true)
    private long recordTime;

    public DailyOfServerData(long newLikeNum, long newTouristNum, long newUserNum, long oldUserNum, long newCommentNum, long newBlogsNum, Set<String> ipHistory, long time) {
        this.newTouristNum = newTouristNum;
        this.newUserNum = newUserNum;
        this.newLikeNum = newLikeNum;
        this.newCommentNum = newCommentNum;
        this.newBlogsNum = newBlogsNum;
        this.oldUserNum = oldUserNum;
        this.ipHistory = ipHistory;
        this.recordTime = TimeUtil.getTimeDayStartTime(time);
    }

    public static DailyOfServerData newInstance(long newTouristNum, long newUserNum, long oldUserNum, long newCommentNum, long newBlogsNum, long newLikeNum, Set<String> ipHistory, long time) {
        return new DailyOfServerData(newLikeNum, newTouristNum, newUserNum, newCommentNum, newBlogsNum, oldUserNum, ipHistory, time);
    }

    public long getIpNum() {
        if (CollectionUtils.isEmpty(ipHistory)) return 0;
        return ipHistory.size();
    }
}
