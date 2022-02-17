package com.donglaistd.jinli.processors.handler.guess;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.GuessDaoService;
import com.donglaistd.jinli.database.dao.GuessWagerRecordDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.Guess;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.GuessUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class GuessWagerDetailsRequestHandler extends MessageHandler {
    @Autowired
    GuessWagerRecordDaoService guessWagerRecordDaoService;
    @Autowired
    GuessDaoService guessDaoService;
    @Autowired
    GuessUtil guessUtil;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user){
        Jinli.GuessWagerDetailsRequest request =  messageRequest.getGuessWagerDetailsRequest();
        var guessOderMsg = guessWagerRecordDaoService.findById(request.getGuessWagerRecordId());
        Jinli.GuessWagerDetailsReply.Builder builder = Jinli.GuessWagerDetailsReply.newBuilder();
        final Guess guessMsg = guessDaoService.findById(guessOderMsg.getGuessId());
        guessOderMsg.getWagerList().forEach((k,v)->{
            Jinli.GuessDetailsItemList.Builder itemList = Jinli.GuessDetailsItemList.newBuilder();
            if(guessMsg.getState() != Constant.GuessState.LOTTERY){
                itemList.setOptionContent( guessOderMsg.getWagerList().get(k).getOptionContent())
                        .setTotalCoin(guessOderMsg.getWagerList().get(k).getBetNum()).build();
                builder.addGuessDetailsItemList(itemList);
            }
            else {
                itemList.setOptionContent( guessOderMsg.getWagerList().get(k).getOptionContent())
                        .setTotalCoin(guessOderMsg.getWagerList().get(k).getBetNum())
                        .setProfitLoss(guessOderMsg.getWagerList().get(k).getBetNum()).build();
                builder.addGuessDetailsItemList(itemList);
            }
        });
        var state =  Constant.GuessState.LOTTERY;
        if(guessMsg.getState() != Constant.GuessState.LOTTERY){
            state = guessUtil.judgeGuessState(guessMsg).getState();
        }
        builder.setGuessId(guessMsg.getId())
                .setGuessWagerRecordId(guessOderMsg.getId().toString())
                .setShowEndTime(guessMsg.getShowEndTime())
                .setGuessImg(guessMsg.getGuessImg())
                .setWagerTime(guessOderMsg.getWagerTime())
                .setState(state)
                .setTotal(guessOderMsg.getTotal()).build();
        return buildReply(builder, Constant.ResultCode.SUCCESS);
    }
}
