package com.donglaistd.jinli.processors.handler.guess;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.GuessDaoService;
import com.donglaistd.jinli.database.entity.backoffice.Guess;
import com.donglaistd.jinli.http.entity.GuessItem;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class GuessWagerInformationRequestHandlerTest extends BaseTest {
    private final static Logger logger = LoggerFactory.getLogger(Jinli.GuessDetailsReply.class);
    @Autowired
    GuessWagerRequestHandler guessWagerRequestHandler;
    @Autowired
    GuessDaoService guessDaoService;
    @Autowired
    InsertGuessTestData insertGuessTestData;
    @Autowired
    GuessWagerInformationRequestHandler guessWagerInformationRequestHandler;

    @Test
    public void getGuessWagerInformation(){
        var guessId = insertGuessTestData.insertGuessData();
        user.setGameCoin(1000000000);
        List<Jinli.GuessWagerList> wagerItemList = new ArrayList<>();
        var itemTest1 = Jinli.GuessWagerList.newBuilder().setBetNum(10).setOptionContent("1");
        var itemTest2 = Jinli.GuessWagerList.newBuilder().setBetNum(10).setOptionContent("2");
        wagerItemList.add(itemTest1.build());
        wagerItemList.add(itemTest2.build());
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GuessWagerRequest send = Jinli.GuessWagerRequest.newBuilder()
                .setGuessId(guessId)
                .setUserId(user.getId())
                .addAllGuessWagerList(wagerItemList).build();
        builder.setGuessWagerRequest(send);
        guessWagerRequestHandler.doHandle(context, builder.build(), user);
        Jinli.JinliMessageRequest.Builder builder1 = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GuessWagerInformationRequest send1 = Jinli.GuessWagerInformationRequest.newBuilder()
                .setUserId(user.getId())
                .setQueryTimeType(Constant.QueryTimeType.ALL).build();
        builder1.setGuessWagerInformationRequest(send1);
        var req =  guessWagerInformationRequestHandler.doHandle(context,builder1.build(),user);
        var loggerMsg = req.getGuessWagerInformationReply().getGuessWagerInformationListList();
        Assert.assertEquals(Constant.ResultCode.SUCCESS,req.getResultCode());
        logger.info(loggerMsg.toString());
    }
}