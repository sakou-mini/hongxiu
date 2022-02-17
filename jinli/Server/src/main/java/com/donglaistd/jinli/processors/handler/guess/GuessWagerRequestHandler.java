package com.donglaistd.jinli.processors.handler.guess;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.CoinFlowDaoService;
import com.donglaistd.jinli.database.dao.GuessDaoService;
import com.donglaistd.jinli.database.dao.GuessWagerRecordDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.CoinFlow;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.GuessWagerRecord;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.http.entity.GuessBetInfo;
import com.donglaistd.jinli.http.service.CoinFlowService;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.GuessUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class GuessWagerRequestHandler extends MessageHandler {
    Logger logger = Logger.getLogger(GuessWagerRequestHandler.class.getName());
    @Autowired
    GuessDaoService guessDaoService;
    @Autowired
    GuessUtil guessUtil;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    GuessWagerRecordDaoService guessWagerRecordDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    CoinFlowService coinFlowService;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.GuessWagerRequest request = messageRequest.getGuessWagerRequest();
        Jinli.GuessWagerReply.Builder builder = Jinli.GuessWagerReply.newBuilder();
        var guessMsg = guessDaoService.findById(request.getGuessId());
        var wagerMsg = request.getGuessWagerListList();
        var userMsg = userDaoService.findById(request.getUserId());
        long nowTime = System.currentTimeMillis();
        if (nowTime > guessMsg.getWagerEndTime()) {
            return buildReply(builder, Constant.ResultCode.GUESS_WAGER_OVER);
        }
        if (nowTime < guessMsg.getWagerStartTime()) {
            return buildReply(builder, Constant.ResultCode.GUESS_WAGER_NOT_START);
        }
        int coinNum = 0;
        for (Jinli.GuessWagerList guessWagerList : wagerMsg) {
            coinNum += guessWagerList.getBetNum();
        }
        if (coinNum > user.getGameCoin()) {
            return buildReply(builder, Constant.ResultCode.NOT_ENOUGH_GAMECOIN);
        }
        if(coinNum <=0){
           logger.info("error bet guess");
            return buildReply(builder, Constant.ResultCode.PARAM_ERROR);
        }
        EventPublisher.publish(new ModifyCoinEvent(user, -coinNum));
        guessDaoService.increaseGuessTotalGameCoinAndTotal(guessMsg, coinNum);
        coinFlowService.setUserCoinFlow(user.getId(),coinNum);
        Map<String, GuessBetInfo> WagerList = new HashMap<>();
        var guessWagerRecord = new GuessWagerRecord();
        for (Jinli.GuessWagerList guessWagerList : wagerMsg) {
            var betInfoMsg = new GuessBetInfo();
            betInfoMsg.setBetNum(guessWagerList.getBetNum());
            betInfoMsg.setOptionContent(guessWagerList.getOptionContent());
            WagerList.put(guessWagerList.getOptionContent(), betInfoMsg);
        }
        guessWagerRecord.setOrderNum(guessWagerRecord.getId().toString());
        guessWagerRecord.setGuessId(request.getGuessId());
        guessWagerRecord.setUserId(request.getUserId());
        guessWagerRecord.setWagerTime(System.currentTimeMillis());
        guessWagerRecord.setTotal((long) coinNum);
        guessWagerRecord.setWagerList(WagerList);
        guessWagerRecordDaoService.save(guessWagerRecord);
        Jinli.GuessWagerReply.Builder builderSend = Jinli.GuessWagerReply.newBuilder();
        builderSend.setWagerTime(guessWagerRecord.getWagerTime());
        builderSend.setWagerRecordId(guessWagerRecord.getOrderNum());
        User userInfo = userDaoService.findById(user.getId());
        builderSend.setGameCoin(userInfo.getGameCoin());
        List<Jinli.GuessWagerList> sendGuessWagerReply = new ArrayList<>();
        for (Map.Entry<String, GuessBetInfo> entry : WagerList.entrySet()) {
            Jinli.GuessWagerList replyBetNum = Jinli.GuessWagerList.newBuilder()
                    .setBetNum(entry.getValue().getBetNum())
                    .setOptionContent(entry.getValue().getOptionContent()).build();
            sendGuessWagerReply.add(replyBetNum);
        }
        builderSend.addAllGuessWagerList(sendGuessWagerReply);
        return buildReply(builderSend, Constant.ResultCode.SUCCESS);
    }
}
