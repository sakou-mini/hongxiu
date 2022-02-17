package com.donglaistd.jinli.util.landlords;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.game.landlord.Landlords;
import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;
import com.donglaistd.jinli.task.pocker.PockBaseTask;
import com.donglaistd.jinli.util.MessageUtil;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LandlordsMessageUtil {
    public static List<Jinli.LandlordCardRecord> buildPlayRecords(Landlords game){
        List<Jinli.LandlordCardRecord> playRecords = new ArrayList<>();
        game.getPlayRecords().forEach(record ->playRecords.add(record.toProto()));
        return playRecords;
    }

    public static List<Jinli.UserRate> buildUserRate(Landlords game){
        List<Jinli.UserRate> userRates = new ArrayList<>();
        game.getPokerPlayers().forEach(player -> userRates.add(Jinli.UserRate.newBuilder().setRate(game.getUserRate(player.getUserId()))
                .setUserId(player.getUserId()).build()));
        return userRates;
    }
    public static  List<Jinli.UserCardsNumberInfo> buildUserCardsSummaryInfo(Landlords game){
        List<Jinli.UserCardsNumberInfo> summaries = new ArrayList<>();
        game.getPokerPlayers().forEach(player -> summaries.add(Jinli.UserCardsNumberInfo.newBuilder().setUserId(player.getUserId())
                .setCardCount(game.getUserCards(player.getUserId()).size()).build()));
        return summaries;
    }

    public static List<Jinli.PokerPlayer> buildPlayers(List<PokerPlayer> players){
        List<Jinli.PokerPlayer> resultPlayers = new ArrayList<>(players.size());
        players.forEach(player -> resultPlayers.add(player.toProto()));
        return resultPlayers;
    }

    public static Jinli.LandlordUserCardsBroadcastMessage buildUserCardsBroadcastMessage(Landlords game,String userId){
        List<Jinli.UserCardsNumberInfo> cardNumberInfos = buildUserCardsSummaryInfo(game);
        List<Card> userCards = game.getUserCards(userId);
        return Jinli.LandlordUserCardsBroadcastMessage.newBuilder().addAllUserCardsNumberInfos(cardNumberInfos)
                .addAllUserCards(MessageUtil.getJinliCard(userCards)).setGameId(game.getGameId()).setStep(game.getStep()).build();
    }

    public static Jinli.GrabLandlordBroadcastMessage buildGrabLandlordBroadcastMessage(Landlords game){
        Jinli.GrabLandlordBroadcastMessage.Builder builder = Jinli.GrabLandlordBroadcastMessage.newBuilder().setStep(game.getStep());
        PockBaseTask countDownTask = game.getCountDownTask();
        if(countDownTask != null)
            builder.setCountDownTime(countDownTask.getCountDownTime()).setCurrentPlayer(countDownTask.getPokerPlayer().toProto());
        return builder.build();
    }

    public static Jinli.GrabLandlordResultBroadcastMessage buildGrabLandlordResultBroadcastMessage(Landlords game, PokerPlayer pokerPlayer){
        return Jinli.GrabLandlordResultBroadcastMessage.newBuilder()
                .setStatue(pokerPlayer.getGrabLordStatue()).setGrabLordPlayer(pokerPlayer.toProto())
                .setStep(game.getStep()).addAllUserRates(buildUserRate(game)).build();
    }

    public static Jinli.GrabLandlordOverInfoBroadcastMessage buildGrabLandlordOverInfoBroadcastMessage(Landlords game,int timeSecond){
        Jinli.GrabLandlordOverInfoBroadcastMessage.Builder builder = Jinli.GrabLandlordOverInfoBroadcastMessage.newBuilder().setStep(game.getStep()).setCountDownTime(timeSecond);
        if(!Strings.isNullOrEmpty(game.getLandlordId())){
            builder.setLandlords(game.getPokerPlayerById(game.getLandlordId()).toProto()).addAllLandlordsCard(MessageUtil.getJinliCard(game.getHoleCards())).setHasLandlords(true);
        }else{
            builder.setHasLandlords(false);
        }
        return builder.build();
    }

    public static Jinli.LandlordChooseRateBroadcastMessage buildChooseRateBroadcastMessage(Landlords game){
        PockBaseTask countDownTask = game.getCountDownTask();
        Jinli.LandlordChooseRateBroadcastMessage.Builder builder = Jinli.LandlordChooseRateBroadcastMessage.newBuilder().setCountDownTime(game.getConfig().getChooseRateCountDownTime()).setStep(game.getStep());
        if(countDownTask.getPokerPlayer()!=null)
            builder.setCurrentPlayer(countDownTask.getPokerPlayer().toProto());
        return builder.build();
    }

    public static Jinli.LandlordChooseRateResultBroadcastMessage buildChooseRateResultBroadcastMessage(PokerPlayer pokerPlayer, boolean chooseResult, Landlords game){
        return Jinli.LandlordChooseRateResultBroadcastMessage.newBuilder().setCurrentPlayer(pokerPlayer.toProto())
                .setStep(game.getStep()).setStatue(chooseResult).addAllUserRates(buildUserRate(game)).build();
    }

    public static Jinli.LandlordChooseRateOverBroadcastMessage buildChooseRateOverBroadcastMessage(Landlords game,int countDownTime){
        return Jinli.LandlordChooseRateOverBroadcastMessage.newBuilder().setGameId(game.getGameId()).setCountDownTime(countDownTime)
                .addAllUserRates(buildUserRate(game)).setStep(game.getStep()).build();
    }

    public static Jinli.PlayLandlordBroadcastMessage buildPlayLandlordBroadcastMessage(PokerPlayer player, long countDownTime, Constant.LandlordsStep step, boolean isNewRound){
        return Jinli.PlayLandlordBroadcastMessage.newBuilder().setCurrentPlayer(player.toProto()).setCountDownTime(countDownTime).setStep(step).setIsNewRound(isNewRound).build();
    }

    public static Jinli.PlayLandlordResultBroadcastMessage buildPlayCardResultBroadcastMessage(PokerPlayer currentPlayer, boolean isPass, Landlords game){
        List<Jinli.UserCardsNumberInfo> userCardNumberInfos = buildUserCardsSummaryInfo(game);
        return Jinli.PlayLandlordResultBroadcastMessage.newBuilder().setCurrentPlayer(currentPlayer.toProto())
                .setStep(game.getStep()).setIsPass(isPass).addAllCardRecord(buildPlayRecords(game)).addAllUserCardsNumberInfos(userCardNumberInfos).build();
    }

    public static Jinli.LandlordGameResultBroadcastMessage buildGameResultBroadcastMessage(Landlords game,long endWaitingTime,boolean hasSpring){
        Jinli.LandlordGameResultBroadcastMessage.Builder builder = Jinli.LandlordGameResultBroadcastMessage.newBuilder().setGameId(game.getGameId())
                .setStep(game.getStep()).setWinnerType(game.getWinnerType()).setNewRoundCountDownTime(endWaitingTime)
                .addAllPlayers(buildPlayers(game.getPokerPlayers())).setHasSpring(hasSpring);
        Map<String, Integer> userWinCoinMap = game.getUserWinOrLostMap();
        userWinCoinMap.forEach((userId,amount)-> builder.addGameAmount(Jinli.LandlordGameAmount.newBuilder()
                .setAmount(amount).setPlayer(game.getPokerPlayerById(userId).toProto())));
        return builder.build();
    }
    public static Jinli.LandlordUserRateUpdateBroadcastMessage buildUserRateUpdateBroadcastMessage(Landlords landlords){
        List<Jinli.UserRate> userRates = new ArrayList<>();
        landlords.getPokerPlayers().forEach(player -> userRates.add(Jinli.UserRate.newBuilder().setRate(landlords.getUserRate(player.getUserId())).setUserId(player.getUserId()).build()));
        return Jinli.LandlordUserRateUpdateBroadcastMessage.newBuilder().addAllUserRates(userRates).build();
    }
}
