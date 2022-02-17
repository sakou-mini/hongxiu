package com.donglaistd.jinli.processors.handler.guess;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class QueryGuessListRequestHandlerTest  extends BaseTest {

    private final static Logger logger = LoggerFactory.getLogger(Jinli.GuessDetailsReply.class);
    @Autowired
    QueryGuessListRequestHandler queryGuessListRequestHandler;
    @Test
    public void getGuessList() {

        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.QueryGuessListRequest send = Jinli.QueryGuessListRequest.newBuilder().setGuessType(Constant.GuessType.GUESS_ALL).build();
        builder.setQueryGuessListRequest(send);
        var req =  queryGuessListRequestHandler.doHandle(context,builder.build(),user);
        var loggerMsg = req.getQueryGuessListReply().getGuessListList();
        Assert.assertEquals(Constant.ResultCode.SUCCESS,req.getResultCode());
        logger.info(loggerMsg.toString());
    }

}
