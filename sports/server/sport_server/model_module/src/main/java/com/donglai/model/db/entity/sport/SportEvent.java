package com.donglai.model.db.entity.sport;

import com.donglai.common.util.StringUtils;
import com.donglai.model.dto.ExtraSportRaceDTO;
import com.donglai.protocol.Live;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

import java.util.Objects;

import static com.donglai.common.util.TimeUtil.timeStrToTimestamp;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SportEvent {
    @Id
    private String id;
    //赛事名
    private String eventName;
    //赛事开始时间
    private long eventDatetime;
    //赛事主队
    private String eventHomeTeam;
    //赛事副队
    private String eventAwayTeam;
    //联赛名稱
    private String eventCompetition;
    //联赛类型
    private String eventSport;

    public SportEvent(ExtraSportRaceDTO raceDTO) {
        this.id = raceDTO.getEventId();
        this.eventName = raceDTO.getEventName();
        this.eventDatetime = timeStrToTimestamp(raceDTO.getEventDatetime());
        this.eventHomeTeam = raceDTO.getEventHomeTeam();
        this.eventAwayTeam = raceDTO.getEventAwayTeam();
        this.eventCompetition = raceDTO.getEventCompetition();
        this.eventSport = raceDTO.getEventSport();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SportEvent that = (SportEvent) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Live.SportEventInfo toProto(){
        Live.SportEventInfo.Builder builder = Live.SportEventInfo.newBuilder().setEventId(id).setEventName(eventName).
                setEventDatetime(String.valueOf(eventDatetime)).setEventHomeTeam(eventHomeTeam)
                .setEventAwayTeam(eventAwayTeam)
                .setEventSport(eventSport);
        if(!StringUtils.isNullOrBlank(eventCompetition))
            builder.setEventCompetition(eventCompetition);
        return builder.build();
    }
}
