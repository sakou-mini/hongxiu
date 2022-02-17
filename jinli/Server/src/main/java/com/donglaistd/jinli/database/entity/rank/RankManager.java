package com.donglaistd.jinli.database.entity.rank;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.DailyIncomeDaoService;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RankDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RankManager {
    public static final long DAY_MILLISECOND = 1000 * 3600 * 24;
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private RankDaoService rankDaoService;
    @Autowired
    private DailyIncomeDaoService dailyIncomeDaoService;
    @Autowired
    private LiveUserDaoService liveUserDaoService;
    @Value("${data.rank.coefficient}")
    private int COEFFICIENT;

    private RankManager() {
    }

    public void calculateLevel(int page, int size) {
        List<User> users = userDaoService.findByPageAndSortByLevel(page, size);
        List<Rank> list = compareWithYesterdayData(users, DataManager.gradeRank, Constant.RankType.LEVEL);
        List<Rank> all = rankDaoService.saveAll(list);
        List<Jinli.Grade> collect = all.stream().map(r -> {
            try {
                User user = userDaoService.findById(r.getUserId());
                return Jinli.Grade.newBuilder()
                        .setDisplayName(user.getDisplayName())
                        .setAvatarUrl(user.getAvatarUrl())
                        .setUserId(String.valueOf(r.getUserId()))
                        .setLevel(r.getExtra().getInteger(Constant.RankType.LEVEL.name()))
                        .setArrow(Constant.Arrow.forNumber(r.getArrow()))
                        .build();
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (!DataManager.gradeRank.isEmpty())
            DataManager.gradeRank.clear();
        DataManager.gradeRank.addAll(collect);
    }

    public void calculateIncome(int size) {
        long endTime = todayStartTime(System.currentTimeMillis());
        long startTime = yesterdayStartTime(endTime);
        List<DailyIncome> incomes = dailyIncomeDaoService.findByTimeBetween(size, startTime, endTime);
        List<Rank> list = compareWithYesterdayData(incomes, DataManager.incomeRank, Constant.RankType.INCOME);
        List<Rank> all = rankDaoService.saveAll(list);
        List<Jinli.DailyIncome> collect = all.stream().map(r -> {
            try {
                User user = userDaoService.findById(r.getUserId());
                if (Objects.isNull(user)) return null;
                return Jinli.DailyIncome.newBuilder()
                        .setAvatarUrl(user.getAvatarUrl())
                        .setUserId(String.valueOf(user.getId()))
                        .setDisplayName(user.getDisplayName())
                        .setAmount(r.getExtra().getInteger(Constant.RankType.INCOME.name()))
                        .setArrow(Constant.Arrow.forNumber(r.getArrow()))
                        .build();
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (!DataManager.incomeRank.isEmpty())
            DataManager.incomeRank.clear();
        DataManager.incomeRank.addAll(collect);
    }

    public void calculatePopularity(int page, int size) {
        List<LiveUserInfo> vos = liveUserDaoService.findByPageAndSortByFollowSize(page, size);
        List<Rank> list = compareWithYesterdayData(vos, DataManager.liveRank, Constant.RankType.POPULARITY);
        List<Rank> all = rankDaoService.saveAll(list);
        List<Jinli.LiveUserRankInfo> collect = all.stream().map(r -> {
            try {
                LiveUser liver = liveUserDaoService.findById(r.getUserId());
                User user = userDaoService.findByLiveUserId(liver.getId());
                return Jinli.LiveUserRankInfo.newBuilder()
                        .setUserId(String.valueOf(liver.getId()))
                        .setDisplayName(user.getDisplayName())
                        .setAvatarUrl(user.getAvatarUrl())
                        .setStatus(liver.getLiveStatus())
                        .setHotValue(r.getExtra().getLong(Constant.RankType.POPULARITY.name()).intValue())
                        .build();
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (!DataManager.liveRank.isEmpty())
            DataManager.liveRank.clear();
        DataManager.liveRank.addAll(collect);
    }

    public long todayStartTime(long nowTime) {
        return nowTime - (nowTime + TimeZone.getDefault().getRawOffset()) % DAY_MILLISECOND;
    }

    public long yesterdayStartTime(long nowTime) {
        return nowTime - DAY_MILLISECOND;
    }

    public List<Rank> compareWithYesterdayData(List<?> newData, List<?> oldData, Constant.RankType rankType) {
        switch (rankType) {
            case POPULARITY:
                Map<LiveUserInfo, Integer> newLiver = new LinkedHashMap<>();
                Map<String, Integer> oldLiver = new LinkedHashMap<>();
                for (int i = 0; i < newData.size(); i++) {
                    newLiver.put((LiveUserInfo) newData.get(i), i);
                }
                for (int i = 0; i < oldData.size(); i++) {
                    Jinli.LiveUserRankInfo liveUserRankInfo = (Jinli.LiveUserRankInfo) oldData.get(i);
                    oldLiver.put(liveUserRankInfo.getUserId(), i);
                }
                return newLiver.keySet().stream().map(l -> {
                    try {
                        Integer newRank = newLiver.getOrDefault(l, 0);
                        Integer oldRank = oldLiver.getOrDefault(String.valueOf(l.getId()), newRank + 1);
                        return buildRank(l.getId(), new Document(Constant.RankType.POPULARITY.name(), l.getHotValue() * COEFFICIENT), Constant.RankType.POPULARITY, newRank, oldRank);
                    } catch (Exception e) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
            case LEVEL:
                Map<User, Integer> newLevel = new LinkedHashMap<>();
                Map<String, Integer> oldLevel = new LinkedHashMap<>();
                for (int i = 0; i < newData.size(); i++) {
                    newLevel.put((User) newData.get(i), i);
                }
                for (int i = 0; i < oldData.size(); i++) {
                    Jinli.Grade grade = (Jinli.Grade) oldData.get(i);
                    oldLevel.put(grade.getUserId(), i);
                }

                return newLevel.keySet().stream().map(user -> {
                    try {
                        Integer newRank = newLevel.getOrDefault(user, 0);
                        Integer oldRank = oldLevel.getOrDefault(String.valueOf(user.getId()), newRank + 1);
                        return buildRank(user.getId(), new Document(Constant.RankType.LEVEL.name(), user.getLevel()), Constant.RankType.LEVEL, newRank, oldRank);
                    } catch (Exception e) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
            case INCOME:
                Map<DailyIncome, Integer> newIncome = new LinkedHashMap<>();
                Map<String, Integer> oldIncome = new LinkedHashMap<>();
                for (int i = 0; i < newData.size(); i++) {
                    newIncome.put((DailyIncome) newData.get(i), i);
                }
                for (int i = 0; i < oldData.size(); i++) {
                    Jinli.DailyIncome dailyIncome = (Jinli.DailyIncome) oldData.get(i);
                    oldIncome.put(dailyIncome.getUserId(), i);
                }
                return newIncome.keySet().stream().map(d -> {
                    try {
                        Integer newRank = newIncome.getOrDefault(d, 0);
                        Integer oldRank = oldIncome.getOrDefault(String.valueOf(d.getUserId()), newRank + 1);
                        return buildRank(d.getUserId(), new Document(Constant.RankType.INCOME.name(), d.getAmount()), Constant.RankType.INCOME, newRank, oldRank);
                    } catch (Exception e) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

    private Rank buildRank(String userId, Document extra, Constant.RankType rankType, int newRank, int oldRank) {
        Rank rank = new Rank();
        rank.setTime(yesterdayStartTime(todayStartTime(System.currentTimeMillis())));
        rank.setRankType(rankType);
        rank.setUserId(userId);
        rank.setExtra(extra);
        if (oldRank < newRank)
            rank.setArrow(Constant.Arrow.DOWNWARD_VALUE);
        else if (oldRank == newRank)
            rank.setArrow(Constant.Arrow.NO_CHANGE_VALUE);
        else
            rank.setArrow(Constant.Arrow.UP_VALUE);
        return rank;
    }
}
