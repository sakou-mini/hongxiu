package com.donglaistd.jinli.database.entity.race;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;
import com.donglaistd.jinli.config.TexasRaceConfig;
import com.donglaistd.jinli.event.AddTexasEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.DefaultImageUtil;
import com.donglaistd.jinli.util.FutureTaskWeakSet;
import com.donglaistd.jinli.util.ScheduledTaskUtil;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;
import static com.donglaistd.jinli.util.MessageUtil.sendMessage;


// 德州赛事
public class TexasRace extends RaceBase {
    private static final Logger logger = Logger.getLogger(TexasRace.class.getName());

    private ObjectId id = ObjectId.get();
    private long startTime;
    private TexasRaceConfig config;
    private List<RacePokerPlayer> joinQueues = new CopyOnWriteArrayList<>();
    private final List<String> gameIds = new ArrayList<>();
    private final FutureTaskWeakSet futureList = new FutureTaskWeakSet();


    public TexasRace() {
    }

    public TexasRace(TexasRaceConfig config) {
        this.config = config;
        setRaceImage(DefaultImageUtil.getRaceImageByRaceTypeAndRaceLevel(getRaceType(),config.getRaceLevel()));
        startTimer();
    }

    public List<String> getGameIds() {
        return gameIds;
    }

    public String getId() {
        return id.toString();
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getStartTime() {
        return (int) (startTime + config.getRegistrationTime() - System.currentTimeMillis());
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public TexasRaceConfig getConfig() {
        return config;
    }

    public void setConfig(TexasRaceConfig config) {
        this.config = config;
    }

    public List<RacePokerPlayer> getJoinQueues() {
        return joinQueues;
    }

    public synchronized void clearJoinQueues() {
        joinQueues.clear();
    }
    public void setJoinQueues(List<RacePokerPlayer> joinQueues) {
        this.joinQueues = joinQueues;
    }

    public synchronized boolean joinRace(User user) {
        if (joinQueues.size() >= config.getMaxPlayers()) return false;
        boolean present = joinQueues.stream().anyMatch(player -> player.getUser().equals(user));
        if (present) return false;
        RacePokerPlayer player = new RacePokerPlayer(user);
        player.setBringInChips(config.getStartingChips());
        DataManager.saveUserRace(user.getId(), UserRace.newInstance(user.getId(), "", getId()));
        joinQueues.add(player);
        sendRewardAmount();
        return true;
    }

    public synchronized boolean quitRace(User user) {
        RacePokerPlayer player = joinQueues.stream().filter(p -> p.getUser().equals(user)).findFirst().orElse(null);
        if (Objects.isNull(player)) return false;
        DataManager.removeUserRace(user.getId());
        boolean remove = joinQueues.remove(player);
        sendRewardAmount();
        return remove;
    }


    @Override
    public Constant.RaceType getRaceType() {
        return Constant.RaceType.TEXAS;
    }

    @Override
    public void removeGame(String gameId) {
        gameIds.remove(gameId);
    }

    @Override
    public int getRaceFee() {
        return config.getRaceFee();
    }

    public void startTimer() {
        updateStartTime();
        saveFuture(ScheduledTaskUtil.schedule(() -> {
            logger.info("Start countdown...");
            if (joinQueues.size() >= config.getMinPlayers()) {
                // startGame
                EventPublisher.publish(new AddTexasEvent(this));
            } else {
                // 重新计时
                startTimer();
            }
        }, config.getRegistrationTime()));
    }

    private void updateStartTime() {
        startTime = System.currentTimeMillis();
        int startTime = getStartTime();
        joinQueues.forEach(p -> {
            Jinli.RaceCountdownBroadcastMessage.Builder builder = Jinli.RaceCountdownBroadcastMessage.newBuilder();
            builder.setCountdown(startTime);
            sendMessage(p.getUser().getId(), buildReply(builder));
        });
    }

    public void cancelTimer() {
        futureList.clear();
    }

    public Jinli.TexasRace toProto() {
        Jinli.TexasRace.Builder builder = Jinli.TexasRace.newBuilder();
        return builder.setRaceId(id.toString())
                .setRaceFee(config.getRaceFee())
                .setServiceCharge(config.getServiceCharge())
                .setTitle(config.getTitle())
                .setRaceType(Constant.RaceType.TEXAS)
                .setRaceImage(getRaceImage())
                .build();
    }
    private void saveFuture(ScheduledFuture<?> future) {
        futureList.add(future);
    }

    public synchronized int getSize() {
        return joinQueues.size();
    }

    private void sendRewardAmount() {
        joinQueues.forEach(p -> {
            Jinli.RewardAmountBroadcastMessage.Builder builder = Jinli.RewardAmountBroadcastMessage.newBuilder();
            builder.setRewardAmount(joinQueues.size() * 1000);
            sendMessage(p.getUser().getId(), buildReply(builder));
        });
    }

    public synchronized void addRaceGame(String gameId) {
        gameIds.add(gameId);
    }
}
