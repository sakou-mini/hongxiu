package com.donglaistd.jinli.processors.handler.guess;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.GuessDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.Guess;
import com.donglaistd.jinli.http.entity.GuessItemProto;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.GuessUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryGuessListRequestHandler extends MessageHandler {

    @Autowired
    GuessDaoService guessDaoService;
    @Autowired
    GuessUtil guessUtil;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Long nowTime = System.currentTimeMillis();
        Jinli.QueryGuessListRequest request = messageRequest.getQueryGuessListRequest();
        Constant.GuessType guessType = request.getGuessType();
        List<Guess> guessArr = new ArrayList<>(0);
        if(guessType != Constant.GuessType.GUESS_ALL){
            guessArr = guessDaoService.findByTypeAndShow(guessType,nowTime);
        }
        else {
            guessArr = guessDaoService.findAllShowGuess(nowTime);
        }
        List<Guess> guessList= new ArrayList<>();
        for(int i=0;i<guessArr.size();i++){
            guessList.add(guessUtil.judgeGuessState(guessArr.get(i)));
        }
        Jinli.QueryGuessListReply.Builder builder = Jinli.QueryGuessListReply.newBuilder();
        for(int i=0;i<guessList.size();i++){
            Jinli.GuessList sendObj = Jinli.GuessList.newBuilder()
                    .setId(guessList.get(i).getId())
                    .setTitle(guessList.get(i).getTitle())
                    .setWindowImg(guessList.get(i).getWindowImg())
                    .setGuessType(guessList.get(i).getGuessType())
                    .setSort(guessList.get(i).getSort())
                    .setState(guessList.get(i).getState())
                    .setTotalCoin(guessList.get(i).getTotalCoin()).build();
            builder.addGuessList(sendObj);
        }
        return buildReply(builder, Constant.ResultCode.SUCCESS);
    }
}
