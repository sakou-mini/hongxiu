package com.donglaistd.jinli.processors.handler.guess;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.GuessDaoService;
import com.donglaistd.jinli.database.dao.GuessWagerRecordDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.GuessWagerRecord;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.GuessUtil;
import com.donglaistd.jinli.util.TimeUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class GuessWagerInformationRequestHandler   extends MessageHandler {
    @Autowired
    GuessWagerRecordDaoService guessWagerRecordDaoService;
    @Autowired
    GuessDaoService guessDaoService;
    @Autowired
    GuessUtil guessUtil;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user){
        Long nowTime = System.currentTimeMillis();
        Jinli.GuessWagerInformationRequest request = messageRequest.getGuessWagerInformationRequest();

        Long queryTime  = 0L;
        if (request.getQueryTimeType() == Constant.QueryTimeType.ALL){
            List<GuessWagerRecord> orders = guessWagerRecordDaoService.findByUserId(request.getUserId());
            var builder = getInformationMsg(orders);
            return buildReply(builder, Constant.ResultCode.SUCCESS);
        }
        else if(request.getQueryTimeType() == Constant.QueryTimeType.TODAY){
            queryTime = TimeUtil.getCurrentDayStartTime();
        }
        else if(request.getQueryTimeType() == Constant.QueryTimeType.WEEK){
            queryTime =TimeUtil.getFirstDayOfCurrentWeeks();
        }
        else if (request.getQueryTimeType() == Constant.QueryTimeType.MONTH){
            queryTime =TimeUtil.getFirstDayOfCurrentMonth();
        }
        List<GuessWagerRecord> orders = guessWagerRecordDaoService.findByWagerTimeAndByUserId(request.getUserId(),queryTime,nowTime);
        var builder = getInformationMsg(orders);
        return buildReply(builder, Constant.ResultCode.SUCCESS);
    }

    public Jinli.GuessWagerInformationReply getInformationMsg(List<GuessWagerRecord> orders){
        Jinli.GuessWagerInformationReply.Builder builder = Jinli.GuessWagerInformationReply.newBuilder();
        for(int i=0;i<orders.size();i++){
            Jinli.GuessWagerInformationList.Builder builderItem = Jinli.GuessWagerInformationList.newBuilder();
            var guessMsg = guessDaoService.findById(orders.get(i).getGuessId());
            long profitLoss = 0L;
            if(guessMsg.getState() == Constant.GuessState.LOTTERY){
                for(int x=0;x<guessMsg.getItemList().size();x++){
                    var guessWagerRecord = orders.get(i).getWagerListItem(guessMsg.getItemList().get(x).getOptionContent());
                    if(guessWagerRecord!=null){
                        profitLoss += guessWagerRecord.getProfitLoss();
                    }
                }
            }
            builderItem.setGuessWagerRecordId(orders.get(i).getId().toString())
                    .setOrderNum(orders.get(i).getOrderNum())
                    .setTotalCoin(orders.get(i).getTotal())
                    .setTitle(guessMsg.getTitle())
                    .setProfitLoss(profitLoss)
                    .setState(guessUtil.judgeGuessState(guessMsg).getState());
            builder.addGuessWagerInformationList(builderItem.build());
        }
        return builder.build();
    }
}
