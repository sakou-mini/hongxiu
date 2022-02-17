package com.donglai.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class ExtraSportRaceDTO {
    //主队
    @JSONField(name = "event_id")
    private String eventId;
    //赛事名
    @JSONField(name = "event_name")
    private String eventName;
    //赛事开始时间
    @JSONField(name = "event_datetime")
    private String eventDatetime;
    //赛事主队
    @JSONField(name = "event_hometeam")
    private String eventHomeTeam;
    //赛事副队
    @JSONField(name = "event_awayteam")
    private String eventAwayTeam;
    //联赛名稱
    @JSONField(name = "event_competition")
    private String eventCompetition;
    //联赛类型
    @JSONField(name = "event_sport")
    private String eventSport;
}
