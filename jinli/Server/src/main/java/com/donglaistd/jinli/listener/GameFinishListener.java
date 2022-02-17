package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.dao.invite.UserBetRecordDaoService;
import com.donglaistd.jinli.database.dao.system.GameServerConfigDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.OfficialLiveRecord;
import com.donglaistd.jinli.database.entity.game.BankerGame;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.database.entity.invite.UserBetRecord;
import com.donglaistd.jinli.database.entity.statistic.DailyBetInfo;
import com.donglaistd.jinli.database.entity.system.GameServerConfig;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.event.GameFinishEvent;
import com.donglaistd.jinli.http.service.CoinFlowService;
import com.donglaistd.jinli.processors.handler.EndLiveRequestHandler;
import com.donglaistd.jinli.service.UserAgentProcessService;
import com.donglaistd.jinli.service.UserOperationService;
import com.donglaistd.jinli.service.statistic.LiveMonitorProcess;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.donglaistd.jinli.constant.GameConstant.EMPTY_GAME_CLOSE_DELAY_TIME;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class GameFinishListener implements EventListener {
    private static final java.util.logging.Logger logger = Logger.getLogger(GameFinishListener.class.getName());

    @Autowired
    DailyIncomeDaoService dailyIncomeDaoService;
    @Autowired
    private LiveUserDaoService liveUserDaoService;
    @Autowired
    private DailyBetInfoDaoService dailyBetInfoDaoService;
    @Autowired
    private OfficialLiveRecordDaoService officialLiveRecordDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    CoinFlowService coinFlowService;
    @Autowired
    UserBetRecordDaoService userBetRecordDaoService;
    @Autowired
    UserAgentProcessService userAgentProcessService;
    @Autowired
    GameServerConfigDaoService gameServerConfigDaoService;
    @Autowired
    EndLiveRequestHandler endLiveRequestHandler;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    UserOperationService userOperationService;
    @Autowired
    LiveMonitorProcess liveMonitorProcess;

    @Override
    public boolean handle(BaseEvent event) {
        var e = (GameFinishEvent) event;
        var game = e.getGame();
        var message = (Jinli.GameResultBroadcastMessage) e.getMessage();
        message = updateGameResultBroadcastUserCoin(message);
        var room = DataManager.roomMap.getOrDefault(game.getOwner().getRoomId(), DataManager.closeRoomInfo.get(game.getOwner().getRoomId()));
        closeGameIfServerIsStop(game);
        if (room == null) {
            logger.warning("Can not find room while processing GameFinishEvent with gameId:" + game.getGameId());
            return true;
        }
        room.broadCastToAllPlatform(buildReply(message, null));
        for (var winner : message.getWinnerAndAmountList()) {
            dailyIncomeDaoService.insert(winner.getUserId(), winner.getWinAmount());
        }
        LiveUser owner = game.getOwner();
        var userBetMap = e.getUserBetAmount();
        if (userBetMap.isEmpty()) return false;
        // add liveUser exp
        var sum = userBetMap.values().stream().mapToLong(Long::longValue).sum();
        long mine = message.getWinnerAndAmountList().stream().filter(s -> s.getUserId().equals(owner.getUserId())).map(Jinli.WinnerAndAmount::getWinAmount).findFirst().orElse(0L);
        long expAward = sum;
        if(mine>0) expAward += mine;
        owner.updateLevel(expAward);
        userBetMap.forEach((betUser,betNum)-> room.addAndGetTotalBetAmount(betUser.getPlatformType(),betNum));
        updateIfOfficialLiveGameAmount(owner.getId(), sum);
        liveUserDaoService.save(owner);
        // add user exp and RecordBetInfo
        process(room,e);
        return true;
    }

    private Jinli.GameResultBroadcastMessage updateGameResultBroadcastUserCoin(Jinli.GameResultBroadcastMessage message){
        Jinli.GameResultBroadcastMessage.Builder gameResultBuilder = message.toBuilder();
        List<Jinli.WinnerAndAmount> winnerAndAmountList = gameResultBuilder.getWinnerAndAmountList();
        gameResultBuilder.clearWinnerAndAmount();
        for (Jinli.WinnerAndAmount winnerAndAmount : winnerAndAmountList) {
            gameResultBuilder.addWinnerAndAmount(winnerAndAmount.toBuilder().setUserCoin(dataManager.findUser(winnerAndAmount.getUserId()).getGameCoin()));
        }
        return gameResultBuilder.build();
    }

    private void process(Room room, GameFinishEvent event) {
        BaseGame game = event.getGame();
        Map<User, Long> betMap = event.getUserBetAmount();
        Map<User, Long> settleMap = game.getUserWinCoinMap();
        List<DailyBetInfo> infos = new ArrayList<>();
        boolean isBankerGame = game instanceof BankerGame && ((BankerGame) game).isOpenBanker();
        betMap.forEach((k, v) -> {
            long winOrLose = 0;
            long exp = v;
            if (settleMap.containsKey(k)) {
                winOrLose = settleMap.get(k);
            }
            userOperationService.updateUserExp(k, exp);
            //k.updateLevel(exp);
            long userCoin = userDaoService.findById(k.getId()).getGameCoin();
            infos.add(new DailyBetInfo(room.getLiveUserId(), k.getId(), System.currentTimeMillis(), v, winOrLose, room.getId(),
                    game.getGameType(), game.getBetAmountMap().get(k), event.getGameResult(), userCoin, isBankerGame,room.getDisplayId()));
            coinFlowService.setUserCoinFlow(k.getId(), v + Math.abs(winOrLose),getServiceFee(v, winOrLose, game.getPayRate()));
            updateUserBetRecord(k.getId(), v);
        });
        dailyBetInfoDaoService.saveAll(infos);
        betMap.keySet().forEach(this::saveLevelAndExperience);
    }

    private void closeGameIfServerIsStop(BaseGame game) {
        GameServerConfig gameServerConfig = gameServerConfigDaoService.findGameServerConfig();
        if (gameServerConfig.isStop()) {

            if (!game.getGameStatus().equals(Constant.GameStatus.ENDED)) {
                logger.warning("server is close ,will end game and end live! ");
                liveMonitorProcess.closeLiveRoom(game.getOwner().getId(), "",EMPTY_GAME_CLOSE_DELAY_TIME, Constant.EndType.NORMAL_END);
            }
        }
    }

    private synchronized void saveLevelAndExperience(User user) {
        User cacheUser = dataManager.findOnlineUser(user.getId());
        if (cacheUser == null) return;
        cacheUser.setLevel(user.getLevel());
        cacheUser.setExperience(user.getExperience());
        cacheUser.setVipType(user.getVipType());
        dataManager.saveUser(cacheUser);
    }

    @Override
    public boolean isDisposable() {
        return false;
    }

    public void updateIfOfficialLiveGameAmount(String liveUserId, long coinAmount) {
        OfficialLiveRecord officialLiveRecord = officialLiveRecordDaoService.findRecentOpenOfficialLiveByLiveUserId(liveUserId);
        if (officialLiveRecord != null) {
            officialLiveRecord.setGamePlayCoinAmount(officialLiveRecord.getGamePlayCoinAmount() + coinAmount);
            officialLiveRecordDaoService.save(officialLiveRecord);
        }
    }

    public void updateUserBetRecord(String userId, long coin) {
        UserBetRecord betRecord = userBetRecordDaoService.findByUserId(userId);
        if (betRecord == null) {
            betRecord = UserBetRecord.newInstance(userId);
        }
        betRecord.addTotalBetCoin(coin);
        userBetRecordDaoService.save(betRecord);
    }

    public int getServiceFee(long betAmount, long winOrLose, BigDecimal payRate){
        if(winOrLose <=0 || betAmount <=0) return 0;
        BigDecimal payRateBeforeAmount = ((BigDecimal.valueOf(winOrLose).subtract(BigDecimal.valueOf(betAmount)).divide(payRate, 4, RoundingMode.HALF_UP)));
        return payRateBeforeAmount.add(BigDecimal.valueOf(betAmount)).subtract(BigDecimal.valueOf(winOrLose)) .intValue();
    }
}
