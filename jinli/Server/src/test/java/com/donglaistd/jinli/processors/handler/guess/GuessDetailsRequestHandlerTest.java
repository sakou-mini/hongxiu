package com.donglaistd.jinli.processors.handler.guess;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.GuessDaoService;
import com.donglaistd.jinli.database.entity.backoffice.Guess;
import com.donglaistd.jinli.http.entity.GuessItem;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class GuessDetailsRequestHandlerTest  extends BaseTest {
    private final static Logger logger = LoggerFactory.getLogger(Jinli.GuessDetailsReply.class);
    @Autowired
    GuessDetailsRequestHandler guessDetailsRequestHandler;
    @Autowired
    GuessDaoService guessDaoService;
    @Test
    public void getGuessMsgTest(){

        var itemListTest = new ArrayList<GuessItem>();
        for(int i=0;i<4;i++){
            var itemTest = new GuessItem();
            itemTest.optionContent = i+"";
            itemListTest.add(itemTest);
        }
        Guess guess = Guess.newInstance("testTitle", "testSubtitle", itemListTest, Constant.GuessType.POLITICS,
                1604454840118L, 1704454840118L, 1604454840119L, 1704454840117L);
        guess.setGuessImg("/img");
        guess.setWindowImg("/IMG");
        guess.setSort(1);
        guess = guessDaoService.save(guess);
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GuessDetailsRequest send = Jinli.GuessDetailsRequest.newBuilder().setGuessId(guess.getId()).build();
        builder.setGuessDetailsRequest(send);
        var req = guessDetailsRequestHandler.doHandle(context,builder.build(),user);
        var loggerMsg = req.getGuessDetailsReply();
        logger.info(loggerMsg.toString());
    }
}
