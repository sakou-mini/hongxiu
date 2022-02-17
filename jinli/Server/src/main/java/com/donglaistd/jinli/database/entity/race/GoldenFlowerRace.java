package com.donglaistd.jinli.database.entity.race;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.GoldenFlowerRaceConfig;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;
import com.donglaistd.jinli.event.AddGoldenFlowerEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.DefaultImageUtil;
import com.donglaistd.jinli.util.ScheduledTaskUtil;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.RaceType.GOLDEN_FLOWER;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
import static com.donglaistd.jinli.util.MessageUtil.sendMessage;

public class GoldenFlowerRace extends RaceBase{
    private static final Logger logger = Logger.getLogger(GoldenFlowerRace.class.getName());

    private ObjectId raceId = ObjectId.get();
    private List<RacePokerPlayer> joinQueues = new CopyOnWriteArrayList<>();
    private final List<String> gameIds = new ArrayList<>();
    private GoldenFlowerRaceConfig config;
    private long startTime;

    public GoldenFlowerRace(GoldenFlowerRaceConfig config) {
        this.config = config;
        startTime = System.currentTimeMillis();
        setRaceImage(DefaultImageUtil.getRaceImageByRaceTypeAndRaceLevel(getRaceType(),config.getRaceLevel()));
        startTimer();
    }

    public static GoldenFlowerRace newInstance(GoldenFlowerRaceConfig config) {
        return new GoldenFlowerRace(config);
    }

    @Override
    public String getId() {
        return raceId.toString();
    }

    @Override
    public boolean joinRace(User user) {
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

    @Override
    public boolean quitRace(User user) {
        RacePokerPlayer player = joinQueues.stream().filter(p -> p.getUser().equals(user)).findFirst().orElse(null);
        if (Objects.isNull(player)) return false;
        DataManager.removeUserRace(user.getId());
        boolean remove = joinQueues.remove(player);
        sendRewardAmount();
        return remove;
    }

    @Override
    public Constant.RaceType getRaceType() {
        return GOLDEN_FLOWER;
    }

    @Override
    public void removeGame(String gameId) {
        gameIds.remove(gameId);
    }

    @Override
    public int getRaceFee() {
        return config.getRaceFee();
    }

    public GoldenFlowerRaceConfig getConfig() {
        return config;
    }
    public synchronized int getSize() {
        return joinQueues.size();
    }
    public void setConfig(GoldenFlowerRaceConfig config) {
        this.config = config;
    }
    public synchronized void addRaceGame(String gameId) {
        gameIds.add(gameId);
    }
    public synchronized void clearJoinQueues() {
        joinQueues.clear();
    }
    public List<RacePokerPlayer> getJoinQueues() {
        return joinQueues;
    }

    public int getStartTime() {
        return (int) (startTime + config.getRegistrationTime() - System.currentTimeMillis());
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setJoinQueues(List<RacePokerPlayer> joinQueues) {
        this.joinQueues = joinQueues;
    }

    public Jinli.GoldenFlowerRace toProto() {
        return Jinli.GoldenFlowerRace.newBuilder().setRaceId(getId())
                .setRaceFee(config.getRaceFee())
                .setRaceType(getRaceType()).setRaceImage(getRaceImage())
                .build();
    }

    public void startTimer() {
        updateStartTime();
        ScheduledTaskUtil.schedule(() -> {
            logger.info("Start countdown...");
            if (joinQueues.size() >= config.getMinPlayers()) {
                EventPublisher.publish(new AddGoldenFlowerEvent(this));
            } else {
                // 重新计时
                startTimer();
            }
        }, config.getRegistrationTime());
    }

    private void sendRewardAmount() {
        joinQueues.forEach(p -> {
            Jinli.RewardAmountBroadcastMessage.Builder builder = Jinli.RewardAmountBroadcastMessage.newBuilder();
            builder.setRewardAmount(joinQueues.size() * config.getBaseReward());
            sendMessage(p.getUser().getId(), buildReply(builder));
        });
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

    public List<String> getGameIds() {
        return gameIds;
    }

}
