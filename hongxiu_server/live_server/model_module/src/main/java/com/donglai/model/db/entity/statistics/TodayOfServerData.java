package com.donglai.model.db.entity.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodayOfServerData implements Serializable {
    //正式用户注册数
    public long totalSignUpNum;
    public long todaySignUpNum;
    //游客数
    public long totalTouristNum;
    public long todayTouristNum;
    //动态
    public long totalBlogsNum;
    public long todayBlogsNum;
    public double blogsWOW; //周同比（今日 -7日前）/七日前
    public double blogsDOD; //日环比 （今日 - 昨日）/昨日 * 100%
    //评论
    public long totalCommentNum;
    public long todayCommentNum;
    public double commentWOW;//周同比（今日 -7日前）/七日前
    public double commentDOD;//日环比 （今日 - 昨日）/昨日
    //更新时间
    public long updateTime;

    public static TodayOfServerData newInstance(long totalSignUpNum, long todaySignUpNum, long totalTouristNum, long todayTouristNum, long totalBlogsNum,
                                                long todayBlogsNum, double blogsWOW, double blogsDOD, long totalCommentNum, long todayCommentNum, double commentWOW, double commentDOD, long time) {
        return new TodayOfServerData(totalSignUpNum, todaySignUpNum, totalTouristNum, todayTouristNum, totalBlogsNum, todayBlogsNum, blogsWOW, blogsDOD, totalCommentNum,
                todayCommentNum, commentWOW, commentDOD, time);
    }
}
