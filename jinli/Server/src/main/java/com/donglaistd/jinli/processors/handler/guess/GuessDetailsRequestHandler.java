package com.donglaistd.jinli.processors.handler.guess;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.GuessDaoService;
import com.donglaistd.jinli.database.dao.GuessWagerRecordDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.Guess;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.GuessUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class GuessDetailsRequestHandler extends MessageHandler {
    @Autowired
    GuessDaoService guessDaoService;
    @Autowired
    GuessUtil guessUtil;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    GuessWagerRecordDaoService guessWagerRecordDaoService;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var guess = guessDaoService.findById(messageRequest.getGuessDetailsRequest().getGuessId());
        Guess guessMsg = guessUtil.guessTotalCalculation(guess);
        List<Jinli.GuessDetailsItemList> itemList = new ArrayList<>();
        Jinli.GuessDetailsItemList.Builder itemMsg = Jinli.GuessDetailsItemList.newBuilder();
        for (int i = 0; i < guessMsg.getItemList().size(); i++) {
            itemMsg.setTotalCoin(guessMsg.getItemList().get(i).getTotalCoin())
                    .setOptionContent(guessMsg.getItemList().get(i).getOptionContent());
            itemList.add(itemMsg.build());
        }
        Jinli.GuessDetailsReply.Builder send = Jinli.GuessDetailsReply.newBuilder();
        send.setTitle(guessMsg.getTitle())
                .setSubtitle(guessMsg.getSubtitle())
                .setGuessImg(guessMsg.getGuessImg())
                .setWagerStartTime(guessMsg.getWagerStartTime())
                .setWagerEndTime(guessMsg.getWagerEndTime())
                .setShowEndTime(guessMsg.getShowEndTime())
                .setTotal(guessMsg.getTotal())
                .setTotalCoin(guessMsg.getTotalCoin())
                .addAllGuessDetailsItemList(itemList)
                .build();

        return buildReply(send, Constant.ResultCode.SUCCESS);
    }
}
