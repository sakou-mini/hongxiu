package com.donglaistd.jinli.database.entity.game.landlord;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.LandlordGameConfig;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.Deck;
import com.donglaistd.jinli.database.entity.game.RaceGame;
import com.donglaistd.jinli.database.entity.race.UserRace;
import com.donglaistd.jinli.event.LandlordsEndEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.task.pocker.*;
import com.donglaistd.jinli.util.*;
import com.donglaistd.jinli.util.landlords.LandlordsMessageUtil;
import com.donglaistd.jinli.util.landlords.LandlordsRankUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.GrabLordStatue.GrabLordStatue_APPLY;
import static com.donglaistd.jinli.Constant.LandlordsStep.*;
import static com.donglaistd.jinli.Constant.LandlordsType.Poker_null;
import static com.donglaistd.jinli.Constant.LandlordsWinnerIdentity.*;
import static com.donglaistd.jinli.constant.GameConstant.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
import static com.donglaistd.jinli.util.MessageUtil.sendMessage;

@Document
public class Landlords extends RaceGame {
    private static final Logger logger = Logger.getLogger(Landlords.class.getName());
    @Id
    protected final ObjectId id = ObjectId.get();

    @Transient
    public List<PokerPlayer> pokerPlayers;

    @Transient
    public List<Card> holeCards = new ArrayList<>();

    @Transient
    private PockBaseTask countDownTask;

    @Transient
    protected String landlordId;

    @Transient
    private int gameRound = 0;

    @Transient
    private int grabLandlordRound = 0;

    @Transient
    private Constant.LandlordsStep step = Ready_step;

    @Transient
    private final List<PokerRecord> playRecords = new ArrayList<>(); //本轮出牌记录
    @Transient
    private final List<String> grabLandLordRecord = new ArrayList<>();
    @Transient
    private final Map<String,Boolean> chooseRateRecords = new LinkedHashMap<>();
    @Transient
    private int nextTurnIndex = -1;

    @Transient
    private Constant.LandlordsWinnerIdentity winnerType = IDENTITY_ILLEGAL;

    @Transient
    private int redoubleCount = 0;

    @Transient
    protected Map<String, Integer> userWinOrLostMap = new HashMap<>();

    @Transient
    public Map<Constant.LandlordsRateType, Integer> commonRates = new HashMap<>();

    @Transient
    private boolean isEnd = false;

    @Transient
    private LandlordGameConfig config;

    @Transient
    private final Map<String, List<Card>> userCards = new HashMap<>();

    @Transient
    private boolean isFinalGame = false;
    @Transient
    private int maxRound = 1;

    @Override
    public String getGameId() {
        return id.toString();
    }

    @Override
    public Constant.GameType getGameType() {
        return Constant.GameType.LANDLORD_GAME;
    }

    public int getGrabLandlordRound() {
        return grabLandlordRound;
    }

    public List<PokerPlayer> getPokerPlayers() {
        return pokerPlayers;
    }

    public PockBaseTask getCountDownTask() {
        return countDownTask;
    }

    public String getLandlordId() {
        return landlordId;
    }

    public Constant.LandlordsStep getStep() {
        return step;
    }

    public void setStep(Constant.LandlordsStep step) {
        this.step = step;
    }

    public Map<String, Integer> getUserWinOrLostMap() {
        return userWinOrLostMap;
    }

    public Constant.LandlordsWinnerIdentity getWinnerType() {
        return winnerType;
    }

    public long getPlayTime(){
        return System.currentTimeMillis() - gameStartTime;
    }

    public synchronized void shutDownGame(){
        isEnd = true;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public LandlordGameConfig getConfig() {
        return config;
    }

    public List<Card> getHoleCards() {
        return holeCards;
    }

    public void setConfig(LandlordGameConfig config) {
        this.config = config;
    }

    public void removeUserCards(String userId,List<Card> cards){
        userCards.get(userId).removeAll(cards);
    }

    public Map<String, List<Card>> getUserCards() {
        return userCards;
    }

    public List<Card> getUserCards(String userId){
        return userCards.getOrDefault(userId, new ArrayList<>(0));
    }

    public PokerRecord getLastPlayRecord(){
        ListIterator<PokerRecord> it = playRecords.listIterator(playRecords.size());
        while (it.hasPrevious()) {
            PokerRecord record = it.previous();
            if(!record.getPokerType().equals(Constant.LandlordsType.Poker_null))
                return record;
        }
        return null;
    }

    public void setNextTurnIndex(int nextTurnIndex) {
        this.nextTurnIndex = nextTurnIndex;
    }

    private void saveGrabLandlordRecord(String userId){
        grabLandLordRecord.add(userId);
    }

    private void saveChooseRateRecord(String userId,boolean statue){
        chooseRateRecords.put(userId, statue);
    }

    public List<Jinli.GrabLandlordRecord> getGrabLandLordRecord() {
        List<Jinli.GrabLandlordRecord> records = new ArrayList<>();
        PokerPlayer player;
        for (String userId : grabLandLordRecord) {
            player = getPokerPlayerById(userId);
            records.add(Jinli.GrabLandlordRecord.newBuilder().setPlayer(player.toProto()).setStatue(player.getGrabLordStatue()).build());
        }
        return records;
    }

    public List<Jinli.LandlordRateRecord> getChooseRateRecords() {
        List<Jinli.LandlordRateRecord> records = new ArrayList<>();
        chooseRateRecords.forEach((k,v)-> records.add(Jinli.LandlordRateRecord.newBuilder().setPlayer(getPokerPlayerById(k).toProto()).setStatue(v).build()));
        return records;
    }

    public void setFinalGame(boolean finalGame) {
        isFinalGame = finalGame;
    }

    public void setMaxRound(int maxRound) {
        this.maxRound = maxRound;
    }

    public List<PokerRecord> getPlayRecords() {
        return playRecords;
    }

    private Landlords(Deck deck, List<PokerPlayer> pokerPlayers, String raceId) {
        this.deck = deck;
        this.pokerPlayers = pokerPlayers;
        this.raceId = raceId;
        this.gameStartTime = System.currentTimeMillis();
        pokerPlayers.forEach(player -> DataManager.saveUserRace(player.getUserId(), UserRace.newInstance(player.getUserId(), getGameId(),getRaceId())));

    }

    public static Landlords newInstance(Deck deck, List<PokerPlayer> pokerPlayers, String raceId){
        return new Landlords(deck, pokerPlayers,raceId);
    }

    public PokerPlayer getPokerPlayerById(String userId){
        return pokerPlayers.stream().filter(pokerPlayer -> pokerPlayer.getUserId().equals(userId)).findFirst().orElse(null);
    }

    public synchronized void readyGame(int readyTime){
        step = Ready_step;
        broadcastGameReadyMessage();
        config.setReadyCountDownTime(readyTime);
        resetRoundGame();
        runCountDownTask(DelayRunTask.newInstance(this, this::startGame,readyTime),readyTime);
    }

    public synchronized void cleanTimeTask() {
        futureTaskWeakSet.clear();
    }

    public synchronized void runCountDownTask(PockBaseTask task,long countDownTime,Boolean ... isClean){
        if(isEnd()) return;
        if(isClean.length<=0 || isClean[0]) {
            cleanTimeTask();
        }
        countDownTask = task;
        futureTaskWeakSet.add(ScheduledTaskUtil.schedule(() -> countDownTask.runTask(), countDownTime));
    }

    private void resetRoundGame(){
        deck.reset();
        deck.shuffle();
        nextTurnIndex = -1;
        redoubleCount = 0;
        winnerType = IDENTITY_ILLEGAL;
        landlordId = null;
        playRecords.clear();
        countDownTask = null;
        pokerPlayers.forEach(PokerPlayer::reset);
        userCards.clear();
        commonRates.clear();
        holeCards.clear();
        grabLandLordRecord.clear();
        chooseRateRecords.clear();
    }

    public int countAllGrabLordRecord(){
        return (int) this.pokerPlayers.stream().filter(PokerPlayer::hasGrabLord).count();
    }

    public int countGrabLordRecord(){
        return (int) this.pokerPlayers.stream().filter(pokerPlayers-> pokerPlayers.getGrabLordStatue().equals(GrabLordStatue_APPLY) ).count();
    }

    private synchronized void updateCommonPokerRate(Constant.LandlordsRateType rateType){
        Integer currentRate = commonRates.getOrDefault(rateType, 0);
        if(currentRate<=0 && rateType.equals(Constant.LandlordsRateType.LANDLORDS_RATE))
            currentRate = 1;
        else {
            if(currentRate<=0)
                currentRate = 1;
            currentRate *= config.getRate(rateType);
        }
        commonRates.put(rateType, currentRate);
    }

    public int getUserRate(String userId) {
        PokerPlayer pokerPlayer = getPokerPlayerById(userId);
        if(pokerPlayer == null) return 0;
        int commonRate = 1;
        int landlordRate = 1;
        if(!StringUtils.isNullOrBlank(landlordId) && !step.equals(GrabLandlord_step)) landlordRate = getPokerPlayerById(landlordId).getChooseRate();
        for (Integer rate : commonRates.values()) commonRate *= rate;
        if (userId.equals(landlordId) && !step.equals(GrabLandlord_step)){
            int farmersRate = pokerPlayers.stream().filter(player -> !player.getUserId().equals(userId)).mapToInt(PokerPlayer::getChooseRate).sum();
            return commonRate * farmersRate * landlordRate;
        } else {
            int chooseRate = pokerPlayer.getChooseRate();
            return commonRate * landlordRate * chooseRate;
        }
    }

    private boolean dealCards() {
        if(pokerPlayers.size() == config.getGamePlayerNum()){
            step = DealCard_step;
            pokerPlayers.forEach(member -> userCards.put(member.getUserId(), deck.dealMultipleCards(LANDLORDS_POKER_BASE_SIZE)));
            userCards.values().forEach(cards->cards.sort(ComparatorUtil.getCardComparatorForGameType(getGameType())));
            broadCastUserCards();
            return true;
        }
        return false;
    }

    public synchronized void startGame() {
        if(dealCards()){
            nextTurnIndex = nextTurnIndex < 0 ? RandomUtil.getRandomInt(0, pokerPlayers.size() - 1, null) : nextTurnIndex;
            runCountDownTask(DelayRunTask.newInstance(this, this::enterGrabLordStep,config.getMessageDelayTime()),config.getMessageDelayTime());
        }
    }

    private void enterGrabLordStep(){
        step = GrabLandlord_step;
        executeGrabLordTask(pokerPlayers.get(nextTurnIndex));
    }
    private synchronized void executeGrabLordTask(PokerPlayer pokerPlayer){
        int delayTime = pokerPlayer.isOpenRobot() ? config.getRobotWaitingTime() : config.getGrabCountDownTime();
        runCountDownTask(RobLordTask.newInstance(pokerPlayer,this,delayTime),delayTime);
        broadcastUserRobLandlord();
    }

    public synchronized boolean grabLandlord(PokerPlayer pokerPlayer, boolean isGrabLandlord){
        if(!step.equals(GrabLandlord_step) || !countDownTask.getPokerPlayer().equals(pokerPlayer)) return false;
        countDownTask.stopRun();
        if(isGrabLandlord){
            pokerPlayer.grabLord();
            landlordId = pokerPlayer.getUserId();
            updateCommonPokerRate(Constant.LandlordsRateType.LANDLORDS_RATE);
        }else {
            pokerPlayer.passGrabLord();
        }
        saveGrabLandlordRecord(pokerPlayer.getUserId());
        broadcastGrabLandlordResult(pokerPlayer);
        checkStepIsOverOrDealChooseLandlord();
        return true;
    }

    private void addLandlordCard(){
        List<Card> landlordCards = userCards.get(landlordId);
        holeCards = deck.dealMultipleCards(LANDLORD_BASE_CARD_SIZE);
        landlordCards.addAll(holeCards);
        landlordCards.sort(ComparatorUtil.getCardComparatorForGameType(getGameType()));
        broadCastUserCards();
    }

    private synchronized void checkStepIsOverOrDealChooseLandlord(){
        if(countAllGrabLordRecord() >= pokerPlayers.size()){
            grabLandlordRound++;
            this.countDownTask.stopRun();
            if(!Strings.isNullOrEmpty(landlordId)){
                grabLandlordRound = 0;
                addLandlordCard();
                getPokerPlayerById(landlordId).becomeLandlord();
                runCountDownTask(DelayRunTask.newInstance(this, this::enterChooseRateStep,config.getMessageDelayTime()),config.getMessageDelayTime());
                broadcastGrabLandlordOverInfoBaseCard(config.getMessageDelayTime());
            }else {
                runCountDownTask(DelayRunTask.newInstance(this,()->readyGame(config.getReadyCountDownTime()),config.getMessageDelayTime()),config.getMessageDelayTime());
                broadcastGrabLandlordOverInfoBaseCard(config.getReadyCountDownTime());
            }
        }else{
            PokerPlayer nextPlayer = getNextOutsPokerPlayer();
            executeGrabLordTask(nextPlayer);
        }
    }

    private synchronized void enterChooseRateStep(){
        this.step = AddPayRate_step ;
        nextTurnIndex = -1;
        startChooseRate();
    }

    public synchronized void startChooseRate(){
        broadcastStartChooseRate();
        List<PokerPlayer> robotPlayers = getPlayerByPlayerType(true);
        runCountDownTask(RedoubleTask.newInstance(getPlayerByPlayerType(false), this,config.getChooseRateCountDownTime()),config.getChooseRateCountDownTime(),false);
        DelayTaskConsumer delayTaskConsumer = DelayTaskConsumer.newInstance();
        if(!robotPlayers.isEmpty()){
            for (int i = 0; i <= robotPlayers.size(); i++) {
                int finalI = i;
                delayTaskConsumer.addExecMethod(()->RedoubleTask.newInstance(Lists.newArrayList(robotPlayers.get(finalI)), this, config.getRobotWaitingTime()).runTask());
            }
            delayTaskConsumer.startRun(config.getRobotWaitingTime());
        }
    }

    public List<PokerPlayer> getPlayerByPlayerType(boolean isRobot){
        return pokerPlayers.stream().filter(pokerPlayer -> pokerPlayer.isOpenRobot() == isRobot).collect(Collectors.toList());
    }


    public synchronized boolean plusRate(PokerPlayer pokerPlayer, boolean isRedouble){
        if(step.equals(AddPayRate_step) && !chooseRateRecords.containsKey(pokerPlayer.getUserId())){
            redoubleCount++;
            if(isRedouble) {
                pokerPlayer.setChooseRate(config.getRate(Constant.LandlordsRateType.CHOOSE_RATE));
            }
            saveChooseRateRecord(pokerPlayer.getUserId(), isRedouble);
            broadcastChooseRateResult(pokerPlayer, isRedouble);
            checkChooseRateIsOver();
            return true;
        }
        return false;
    }

    private void checkChooseRateIsOver(){
        if(redoubleCount == pokerPlayers.size()){
            cleanTimeTask();
            runCountDownTask(DelayRunTask.newInstance(this, this::enterPlayCardStep,config.getMessageDelayTime() ),config.getMessageDelayTime());
            broadcastChooseRateOverBroadcastMessage(config.getMessageDelayTime());
        }
    }

    private synchronized void enterPlayCardStep(){
        nextTurnIndex = -1;
        step = PlayCard_step;
        beginPlayCards();
    }

    public synchronized void beginPlayCards(){
        countDownTask.stopRun();
        PokerPlayer playCardsPlayer = getNextOutsPokerPlayer();
        int delayTime = playCardsPlayer.isOpenRobot() ? config.getRobotWaitingTime() : config.getPlayCardCountDownTime();
        runCountDownTask(PlayCardsTask.newInstance(playCardsPlayer, this,delayTime),delayTime);
        broadcastUserPlayCard();
    }

    private void addRateIfIsBomb(Constant.LandlordsType landlordsType){
        if (landlordsType.equals(Constant.LandlordsType.Poker_jokerBomb) || landlordsType.equals(Constant.LandlordsType.Poker_bomb)) {
            updateCommonPokerRate(Constant.LandlordsRateType.BOMB_RATE);
            broadcastUserRateUpdate();
        }
    }
    private synchronized void addPlayRecord(PokerRecord pokerRecord){
        if(isNewRound()){
            playRecords.clear();
        }
        playRecords.add(pokerRecord);
    }

    public synchronized boolean playCards(PokerRecord pokerRecord, PokerPlayer player) {
        if(step.equals(PlayCard_step) && countDownTask.getPokerPlayer().equals(player)){
            countDownTask.stopRun();
            boolean isPass = false;
            addPlayRecord(pokerRecord);
            if(pokerRecord.getCardsSize()<=0 || pokerRecord.getPokerType().equals(Poker_null)){
                isPass = true;
            }else{
                player.addPlayCount();
                removeUserCards(player.getUserId(), pokerRecord.getCards());
                addRateIfIsBomb(pokerRecord.getPokerType());
                broadCastUserCards();
            }
            broadcastUserPlayCardResult(player,isPass);
            if(checkUserCardsIsOver(player)) {
                endGame();
            }else{
                beginPlayCards();
            }
            return true;
        }
        return false;
    }

    @Override
    public void endGame() {
        runCountDownTask(DelayRunTask.newInstance(this,this:: enterSettleStep,config.getMessageDelayTime()),config.getMessageDelayTime());
    }

    public boolean checkUserCardsIsOver(PokerPlayer player){
        if(getUserCards(player.getUserId()).isEmpty()){
            winnerType = landlordId.equals(player.getUserId()) ? LANDLORD : FARMER;
            logger.info("settling！"+ winnerType+"Win==========="+ id);
            return true;
        }
        return false;
    }

    //settle===========
    public synchronized void enterSettleStep(){
        cleanTimeTask();
        step = Settle_step;
        processGameResult();
    }

    private boolean isSpring(){
        if(winnerType.equals(LANDLORD)){
            return getUserCards().entrySet().stream().noneMatch(userCards -> !userCards.getKey().equals(landlordId) && userCards.getValue().size() < LANDLORDS_POKER_BASE_SIZE);
        }
        return winnerType.equals(FARMER) && getPokerPlayerById(landlordId).getPlayCardCount() <= 1;
    }

    private boolean isWinner(String userId){
        if(winnerType.equals(LANDLORD)) return userId.equals(landlordId);
        else if(winnerType.equals(FARMER)) return !userId.equals(landlordId);
        return false;
    }

    public void  dealSettleLandlords(){
        if(isSpring()){
            updateCommonPokerRate(Constant.LandlordsRateType.SPRING_RATE);
        }
        for (PokerPlayer player : pokerPlayers) {
            int userRate = config.getBaseRate()*getUserRate(player.getUserId());
            if(!isWinner(player.getUserId())) {
                userRate = -userRate;
            }else{
                player.addWinRoundRecord(gameRound);
            }
            player.addCoin(userRate);
            userWinOrLostMap.put(player.getUserId(), userRate);
        }
        LandlordsRankUtil.rankPlayersByStartRank(pokerPlayers,FIRST_RANK);
    }


    private boolean verifyGameIsOver(){
        if(!isFinalGame && getPlayTime() > config.getGameMaxPlayTime())
            return true;
        if(gameRound >= maxRound || isEnd())
            return true;
        return pokerPlayers.stream().anyMatch(player -> player.getInitCoin() <= config.getGameMinCoin());
    }

    public synchronized void processGameResult(){
        gameRound++;
        dealSettleLandlords();
        int countDownTime = config.getEndWaitTime();
        boolean hasSpring = commonRates.containsKey(Constant.LandlordsRateType.SPRING_RATE);
        if(hasSpring) countDownTime += config.getSpringDelayTime();
        if(verifyGameIsOver()){
            runCountDownTask(DelayRunTask.newInstance(this,()->EventPublisher.publish(new LandlordsEndEvent(this)),countDownTime),countDownTime);
        }else{
            runCountDownTask(NewGameTask.newInstance(this,countDownTime),countDownTime);
        }
        broadcastGameResult(countDownTime,hasSpring);
    }


    public PokerPlayer getNextOutsPokerPlayer() {
        if(getLastPlayRecord() == null && nextTurnIndex < 0){
            PokerPlayer nextPokerPlayer = getPokerPlayerById(landlordId);
            nextTurnIndex = pokerPlayers.indexOf(nextPokerPlayer);
            return nextPokerPlayer;
        }else{
            return getNextPlayer();
        }
    }

    public PokerPlayer getNextPlayer(){
        if(nextTurnIndex == (pokerPlayers.size() -1)|| nextTurnIndex < 0)
            nextTurnIndex = 0;
        else
            nextTurnIndex += 1;
        return pokerPlayers.get(nextTurnIndex);
    }

    public void broadcastGameReadyMessage(){
        Jinli.LandlordRaceBeginBroadcastMessage.Builder builder = Jinli.LandlordRaceBeginBroadcastMessage.newBuilder().setGameInfo(toSimpleGameProto());
        broadcast(buildReply(builder));
    }

    public void broadcastGameResult(int nextRoundWaitingTime,boolean hasSpring){
        broadcast(buildReply(LandlordsMessageUtil.buildGameResultBroadcastMessage(this,nextRoundWaitingTime,hasSpring)));
    }

    private void broadcastUserRobLandlord(){
        broadcast(buildReply(LandlordsMessageUtil.buildGrabLandlordBroadcastMessage(this)));
    }

    private void broadcastGrabLandlordResult(PokerPlayer player) {
        broadcast(buildReply(LandlordsMessageUtil.buildGrabLandlordResultBroadcastMessage(this,player)));
    }

    private void broadcastGrabLandlordOverInfoBaseCard(int timeSecond){
        broadcast(buildReply(LandlordsMessageUtil.buildGrabLandlordOverInfoBroadcastMessage(this,timeSecond)));
    }

    private void broadcastStartChooseRate(){
        broadcast(buildReply(LandlordsMessageUtil.buildChooseRateBroadcastMessage(this)));
    }

    private void broadcastChooseRateResult(PokerPlayer pokerPlayer, boolean chooseStaue){
        broadcast(buildReply(LandlordsMessageUtil.buildChooseRateResultBroadcastMessage(pokerPlayer,chooseStaue,this)));
    }

    private void broadcastChooseRateOverBroadcastMessage(int countDownTime){
        broadcast(buildReply(LandlordsMessageUtil.buildChooseRateOverBroadcastMessage(this,countDownTime)));
    }

    public boolean isNewRound(){
        return getLastPlayRecord() == null || Objects.equals(countDownTask.getPokerPlayer().getUserId(),getLastPlayRecord().getUserId());
    }

    private void broadcastUserPlayCard(){
        boolean isNewRound = false;
        if(isNewRound()) {
            isNewRound = true;
        }
        broadcast(buildReply(LandlordsMessageUtil.buildPlayLandlordBroadcastMessage(countDownTask.getPokerPlayer(),countDownTask.getCountDownTime(),getStep(),isNewRound)));
    }

    private void broadcastUserPlayCardResult(PokerPlayer player, boolean isPass){
        broadcast(buildReply(LandlordsMessageUtil.buildPlayCardResultBroadcastMessage(player,isPass,this)));
    }

    private void broadcastUserRateUpdate(){
        broadcast(buildReply(LandlordsMessageUtil.buildUserRateUpdateBroadcastMessage(this)));
    }

    public void broadCastUserCards(){
        userCards.forEach((userId,cards)->broadCastToUser(userId,buildReply(LandlordsMessageUtil.buildUserCardsBroadcastMessage(this, userId))));
    }


    public void broadcast(Jinli.JinliMessageReply message) {
        if(isEnd()) return;
        List<String> users = pokerPlayers.stream().map(PokerPlayer::getUserId).collect(Collectors.toList());
        for (var userId : users) {
            sendMessage(userId, message);
        }
    }

    public void broadCastToUser(String userId , Jinli.JinliMessageReply message) {
        if(isEnd()) return;
        sendMessage(userId, message);
    }


    public Jinli.LandlordsGameInfo toSimpleGameProto(){
        Jinli.LandlordsGameInfo.Builder builder = Jinli.LandlordsGameInfo.newBuilder();
        builder.setGameId(getGameId()).setRaceId(getRaceId()).setRound(gameRound).setStep(this.step)
                .setBaseScore(config.getBaseRate())
                .addAllUserRates(LandlordsMessageUtil.buildUserRate(this)).setMaxRate(POKER_MAX_RATE)
                .addAllPlayers(LandlordsMessageUtil.buildPlayers(pokerPlayers));
        if(step.equals(Ready_step)) builder.setReadyTimeCountDown(config.getReadyCountDownTime());
        return builder.build();
    }

    public Jinli.LandlordsGameInfo toProto(){
        Jinli.LandlordsGameInfo.Builder builder = toSimpleGameProto().toBuilder();
        if(!holeCards.isEmpty())
            builder.addAllLandlordsCard(MessageUtil.getJinliCard(holeCards));
        return builder.addAllCardRecord(LandlordsMessageUtil.buildPlayRecords(this))
                .addAllUserCardsNumberInfos(LandlordsMessageUtil.buildUserCardsSummaryInfo(this)).build();
    }
}
