package com.donglaistd.jinli.service;

import com.donglaistd.jinli.database.dao.GiftLogDaoService;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.util.RankComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.donglaistd.jinli.constant.CacheNameConstant.FANS_CONTRIBUTE_RANK;
import static com.donglaistd.jinli.constant.CacheNameConstant.getFansContributeRankKey;

@Service
public class FansContributionService {
    @Autowired
    RankComponent rankComponent;
    @Autowired
    GiftLogDaoService giftLogDaoService;
    @Autowired
    StringRedisTemplate redisTemplate;

    //粉丝贡献榜
    public List<GiftLog> queryFansContributionRank(String userId, int size){
        loadFansContributionRank(userId);
        return getTopContributionRankInfo(size, userId);
    }

    public boolean loadFansContributionRank(String userId){
        String rankKey = getFansContributeRankKey(userId);
        if(!rankComponent.hasKey(rankKey)){
            long endTime = System.currentTimeMillis();
            long startTime = -1;
            List<GiftLog> giftLogs = giftLogDaoService.findByLiveUserIdAndGroupSenderIdNotLimit(userId, startTime, endTime);
            for (GiftLog giftLog : giftLogs) {
                rankComponent.add(rankKey,giftLog.getSenderId(),-giftLog.getSendAmount());
            }
            return true;
        }
        return false;
    }

    public void updateContributionScore(String sender , String receiverId, int amount){
        if(!loadFansContributionRank(receiverId)){
            String rankKey = getFansContributeRankKey(receiverId);
            rankComponent.improve(rankKey,sender,-amount);
        }
    }

    /*4.query Top n rankInfo*/
    public List<GiftLog> getTopContributionRankInfo(int n,String userId) {
        String key = getFansContributeRankKey(userId);
        Set<ZSetOperations.TypedTuple<String>> typedTuples = rankComponent.rangeWithScore(key, 0, n - 1);
        return buildRedisRankToBizDo(typedTuples);
    }

    private List<GiftLog> buildRedisRankToBizDo(Set<ZSetOperations.TypedTuple<String>> typedTuples) {
        List<GiftLog> rankInfos = new ArrayList<>(typedTuples.size());
        for (ZSetOperations.TypedTuple<String> sub : typedTuples) {
            GiftLog giftLog = new GiftLog();
            giftLog.setSenderId(sub.getValue());
            giftLog.setSendAmount(new Double(Math.abs(sub.getScore())).intValue());
            rankInfos.add(giftLog);
        }
        return rankInfos;
    }

    public void cleanAllRankInfo(){
        redisTemplate.delete(redisTemplate.keys(FANS_CONTRIBUTE_RANK + "*"));
    }
}
