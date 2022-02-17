package com.donglaistd.jinli.database.entity.race;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.LandlordRaceConfig;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.DefaultImageUtil;
import com.donglaistd.jinli.util.MessageUtil;
import com.donglaistd.jinli.util.landlords.LandLordsDataUtil;
import com.donglaistd.jinli.util.landlords.LandlordsMessageUtil;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.RaceStep.*;
import static com.donglaistd.jinli.constant.GameConstant.PREDICT_DEFAULT_TIME;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

public class LandlordsRace extends RaceBase{

    private static final Logger logger = Logger.getLogger(LandlordsRace.class.getName());

    private ObjectId raceId = ObjectId.get();

    private Constant.RaceStep step = Race_Open;

    private final LandlordRaceConfig raceConfig;

    private final List<PokerPlayer> joinQueues = new ArrayList<>();

    private final List<String> gameIds = new ArrayList<>();

    private final List<PokerPlayer> promotionPLayers = new ArrayList<>();

    private LandlordsRace(LandlordRaceConfig raceConfig) {
        this.createTime = System.currentTimeMillis();
        this.raceConfig = raceConfig;
        setRaceImage(DefaultImageUtil.getRaceImageByRaceTypeAndRaceLevel(getRaceType(),raceConfig.getRaceLevel()));
        LandLordsDataUtil.mockJoinRace(this);
    }

    public static LandlordsRace newInstance(LandlordRaceConfig landlordRaceConfig){
        return new LandlordsRace(landlordRaceConfig);
    }

    @Override
    public void removeGame(String gameId){
        gameIds.remove(gameId);
    }

    @Override
    public int getRaceFee() {
        return raceConfig.getRaceFee();
    }

    public boolean containUser(User user){
        return joinQueues.stream().anyMatch(player -> player.getUser().equals(user));
    }

    @Override
    public synchronized boolean quitRace(User user){
        if (!this.step.equals(Race_Open)) {
            return false;
        }
        PokerPlayer pokerPlayer = joinQueues.stream().filter(player -> player.getUser().equals(user)).findAny().orElse(null);
        broadCastRaceInfo();
        DataManager.removeUserRace(user.getId());
        return joinQueues.remove(pokerPlayer);
    }

    @Override
    public Constant.RaceType getRaceType() {
        return Constant.RaceType.LANDLORDS;
    }

    @Override
    public synchronized boolean joinRace(User user){
        boolean present = joinQueues.stream().anyMatch(player -> player.getUser().equals(user));
        if(!this.step.equals(Race_Open) || joinQueues.size()>= raceConfig.getJoinPeopleNum() || present) {
            return false;
        }
        PokerPlayer pokerPlayer = PokerPlayer.newInstance(user, 0, raceConfig.getBaseCoin());
        DataManager.saveUserRace(user.getId(), UserRace.newInstance(user.getId(), "", getId()));
        joinQueues.add(pokerPlayer);
        if(joinQueues.size() == raceConfig.getJoinPeopleNum()) {
            this.step = Race_FirstRound;
        }
        broadCastRaceInfo();
        return true;
    }

    public synchronized void enterFinalRace(PokerPlayer player){
        if(this.step.equals(Race_FirstRound) && !promotionPLayers.contains(player) && promotionPLayers.size()< raceConfig.getGamePeopleNum()) {
            logger.info(player.getUser().getDisplayName()+" enter final race！================》");
            player.reset();
            player.getWinRoundRecord().clear();
            player.setInitCoin(raceConfig.getBaseCoin());
            player.setRank(promotionPLayers.size() + 1);
            player.setSeatNum(promotionPLayers.size() + 1);
            promotionPLayers.add(player);
            broadCastFinalRaceInfo();
            if(gameIds.isEmpty() && promotionPLayers.size() == raceConfig.getGamePeopleNum()){
                this.step = Race_LastRound;
                logger.info("start Final race！-------------->"+ getId());
            }
        }else{
            logger.info("enter Final Race！failed-------------->"+ getId());
        }
    }

    public synchronized void weekOutPlayer(PokerPlayer player){
        joinQueues.remove(player);
    }
    public String getId() {
        return raceId.toString();
    }

    public void setRaceId(ObjectId raceId) {
        this.raceId = raceId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Constant.RaceStep getStep() {
        return step;
    }

    public void setStep(Constant.RaceStep step) {
        this.step = step;
    }

    public LandlordRaceConfig getRaceConfig() {
        return raceConfig;
    }

    public List<String> getGameIds() {
        return gameIds;
    }

    public void addGameId(String gameId){
        this.gameIds.add(gameId);
    }

    public List<PokerPlayer> getJoinQueues() {
        return joinQueues;
    }

    public List<PokerPlayer> getPromotionPLayers() {
        return promotionPLayers;
    }

    public  Jinli.LandlordRace toProto(){
        Jinli.LandlordRace.Builder raceInfo = Jinli.LandlordRace.newBuilder().setCreateTime(getCreateTime()).setRaceId(getId())
                .setJoinNum(joinQueues.size()).setRaceConfig(buildConfigProto()).setRaceType(Constant.RaceType.LANDLORDS).setRaceStep(getStep()).setLeftGameNumber(gameIds.size())
                .addAllPromotionPLayers(LandlordsMessageUtil.buildPlayers(promotionPLayers)).setRaceTitle("3000 coin race")
                .setRaceImage(getRaceImage());
        if(!promotionPLayers.isEmpty() && step.equals(Race_FirstRound)){
            raceInfo.setPredictFinishTime(PREDICT_DEFAULT_TIME*gameIds.size());
        }
        return raceInfo.build();
    }

    public  Jinli.LandlordRaceConfig buildConfigProto(){
        return Jinli.LandlordRaceConfig.newBuilder().setBaseScore(raceConfig.getBaseCoin())
                .setFirstCoin(raceConfig.getRankAward(1)).setSecondCoin(raceConfig.getRankAward(2))
                .setThirdCoin(raceConfig.getRankAward(3)).setJoinFee(raceConfig.getRaceFee()).setMaxJoinNum(raceConfig.getJoinPeopleNum()).build();
    }

    public void broadCastRaceInfo(){
        Jinli.LandlordRaceBroadcastMessage.Builder builder = Jinli.LandlordRaceBroadcastMessage.newBuilder().setRace(toProto());
        joinQueues.forEach( player -> MessageUtil.sendMessage(player.getUserId(), buildReply(builder)) );
    }

    public void broadCastFinalRaceInfo(){
        long predictFinishTime = PREDICT_DEFAULT_TIME*gameIds.size();
        Jinli.LandlordRaceFinalRaceBroadcastMessage.Builder builder;
        for (PokerPlayer player : promotionPLayers) {
            builder = Jinli.LandlordRaceFinalRaceBroadcastMessage.newBuilder().setPlayer(player.toProto()).setLeftGameNumber(gameIds.size())
                    .setPredictFinishTime(predictFinishTime);
            MessageUtil.sendMessage(player.getUserId(),buildReply(builder));
        }
    }
}
