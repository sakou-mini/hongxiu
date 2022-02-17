package com.donglai.model.db.entity.live;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.protocol.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Document(collection = "giftRank")
@Data
@NoArgsConstructor
public class GiftRank implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @AutoIncKey
    private long id;
    @Field
    private long totalTime;
    @Field
    @Indexed(unique = true)
    private Constant.RankType rankType;
    private List<RankInfo> rankInfos;

    private GiftRank(Constant.RankType rankType, List<RankInfo> rankInfos) {
        this.rankType = rankType;
        this.rankInfos = rankInfos;
        this.totalTime = System.currentTimeMillis();
    }

    public static GiftRank newInstance(Constant.RankType rankType, List<RankInfo> rankInfos) {
        return new GiftRank(rankType, rankInfos);
    }

    @Data
    @NoArgsConstructor
    public static class RankInfo{
        private String userId;
        private long score;

        private RankInfo(String userId, long score) {
            this.userId = userId;
            this.score = score;
        }

        public static RankInfo newInstance(String userId, long score){
            return new RankInfo(userId, score);
        }
    }
}
