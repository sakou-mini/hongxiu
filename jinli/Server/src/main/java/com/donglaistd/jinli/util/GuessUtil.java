package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.GuessDaoService;
import com.donglaistd.jinli.database.dao.GuessWagerRecordDaoService;
import com.donglaistd.jinli.database.entity.backoffice.Guess;
import com.donglaistd.jinli.database.entity.backoffice.GuessWagerRecord;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.http.entity.GuessBetInfo;
import com.donglaistd.jinli.http.entity.GuessItem;
import com.donglaistd.jinli.http.service.CoinFlowService;
import com.donglaistd.jinli.service.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;


@Component
public class GuessUtil {
    @Autowired
    GuessDaoService guessDaoService;
    @Autowired
    GuessWagerRecordDaoService guessWagerRecordDaoService;
    @Autowired
    CoinFlowService coinFlowService;

    public Guess judgeGuessShow(Guess guess){
        Long nowTime = System.currentTimeMillis();
        Long showStartTime = guess.getShowStartTime();
        Long showEndTime = guess.getShowEndTime();
        if(showStartTime > nowTime || showEndTime < nowTime){
            guess.setIsShow(Constant.GuessShow.INVISIBLE);
        }
        else {
            guess.setIsShow(Constant.GuessShow.SHOW);
        }
        return guess;
    }

    public Guess judgeGuessState(Guess guess){
        if(guess.getState() != Constant.GuessState.LOTTERY && guess.getState() != Constant.GuessState.PULL_OFF_SHELVES ) {
            Long nowTime = System.currentTimeMillis();
            Long wagerStartTime = guess.getWagerStartTime();
            Long wagerEndTime = guess.getWagerEndTime();
            if (wagerStartTime > nowTime) {
                guess.setState(Constant.GuessState.NOT_WAGER);
            } else if (wagerStartTime < nowTime && nowTime < wagerEndTime) {
                guess.setState(Constant.GuessState.WAGER);
            } else {
                guess.setState(Constant.GuessState.WAGER_OVER);
            }
        }
        return guess;
    }

    public Guess guessTotalCalculation(Guess guess){
        var guessItems = guess.getItemList();
        var orders = guessWagerRecordDaoService.findByGuessId(guess.getId());
        for (GuessWagerRecord order : orders) {
            for (GuessItem guessItem : guessItems) {
                GuessBetInfo wagerListItem = order.getWagerListItem(guessItem.getOptionContent());
                if(wagerListItem!= null) {
                    var itemCoin =wagerListItem.getBetNum();
                    guessItem.setTotalCoin(guessItem.getTotalCoin()+itemCoin);
                    guessItem.setTotalPeople(guessItem.getTotalPeople()+1);
                }
            }
        }
        return guess;
    }

    @Transactional
    public boolean settleGuess(String guessId, String win){
        var guessMsg = guessDaoService.findById(guessId);
        guessMsg.setWin(win);
        guessMsg.setState(Constant.GuessState.LOTTERY);
        guessDaoService.save(guessMsg);
        GuessItem winItemMsg = new GuessItem();
        var lostCoinTotal = 0;
        for (GuessItem item : guessMsg.getItemList()) {
            if (item.getOptionContent().equals(win)){
                winItemMsg.setOptionContent(win);
                winItemMsg.setTotalCoin(item.getTotalCoin());
                winItemMsg.setTotalPeople(item.getTotalPeople());
            }else {
                lostCoinTotal += item.getTotalCoin();
            }
        }
        var guessOder = guessWagerRecordDaoService.findByGuessId(guessId);
        Map<String, Long> userTotalWinMap = new HashMap<>();
        for (GuessWagerRecord record : guessOder) {
            int finalLostCoinTotal = lostCoinTotal;
            record.getWagerList().forEach((itemKey, betInfo) -> {
                if (itemKey.equals(win)) {    //win
                    BigDecimal winCoinFloat = BigDecimal.valueOf(betInfo.getBetNum()).multiply(BigDecimal.valueOf(finalLostCoinTotal))
                            .divide(BigDecimal.valueOf(winItemMsg.getTotalCoin()), 4, RoundingMode.UP)
                            .add(BigDecimal.valueOf(betInfo.getBetNum())).multiply(BigDecimal.valueOf(0.9));
                    long winCoin = winCoinFloat.setScale(0, RoundingMode.UP).longValue();
                    userTotalWinMap.compute(record.getUserId(), (k, v) -> v == null ? winCoin : v + winCoin);
                    betInfo.setProfitLoss(betInfo.getBetNum());
                } else {                  //lose
                    betInfo.setProfitLoss(-betInfo.getBetNum());
                }
            });
            guessWagerRecordDaoService.save(record);
        }
        userTotalWinMap.forEach((k, v) -> coinFlowService.setUserCoinFlow(k, v));
        ModifyCoinEvent modifyCoinEvent = new ModifyCoinEvent(userTotalWinMap);
        EventPublisher.publish(modifyCoinEvent);
        return true;
    }
}