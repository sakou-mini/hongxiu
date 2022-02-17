package com.donglaistd.jinli.processors.handler.race.landlord;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.landlord.Landlords;
import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;
import com.donglaistd.jinli.database.entity.game.landlord.PokerRecord;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.MessageUtil;
import com.donglaistd.jinli.util.landlords.LandlordsPokerUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.donglaistd.jinli.Constant.LandlordsType.Poker_null;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class PlayCardsForLandlordRequestHandler extends MessageHandler {

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.PlayCardsForLandlordRequest request = messageRequest.getPlayCardsForLandlordRequest();
        Jinli.PlayCardsForLandlordReply.Builder reply = Jinli.PlayCardsForLandlordReply.newBuilder();
        Landlords landlordGame = (Landlords) DataManager.findGame(request.getGameId());
        List<Card> playCards = new ArrayList<>();
        if(request.getCardsList() != null){
            playCards = MessageUtil.getGameCards(request.getCardsList());
        }
        PokerRecord playPoker = PokerRecord.newInstance(playCards, user.getId());
        Constant.ResultCode resultCode = verifyUserPlayCard(landlordGame, user, playPoker);
        if(!resultCode.equals(Constant.ResultCode.SUCCESS))  return buildReply(reply, resultCode);
        PokerPlayer player = landlordGame.getPokerPlayerById(user.getId());
        if(!landlordGame.playCards(playPoker, player)) return buildReply(reply, Constant.ResultCode.GAME_OPERATION_ILLEGAL);
        return  buildReply(reply, Constant.ResultCode.SUCCESS);
    }

    private Constant.ResultCode verifyUserPlayCard(Landlords game,User user,PokerRecord playPoker){
        if(Objects.isNull(game)) return Constant.ResultCode.GAME_NOT_EXISTS;
        if(playPoker.getCardsSize() > 0 && playPoker.getPokerType().equals(Poker_null)) return Constant.ResultCode.NO_CARD_TYPE;
        if(!game.getUserCards(user.getId()).containsAll(playPoker.getCards())) return Constant.ResultCode.CARDS_NOT_EXIST;
        PokerRecord pokerRecord = game.getLastPlayRecord();
        if(pokerRecord == null || pokerRecord.getUserId().equals(user.getId())){
            if(playPoker.getCardsSize() <= 0) return Constant.ResultCode.CANNOT_PASS;
        }else if(!playPoker.getCards().isEmpty()){
            if(!LandlordsPokerUtil.comparePoker(pokerRecord, playPoker)) return Constant.ResultCode.CARDS_COMPARE_FAILED;
        }
        return Constant.ResultCode.SUCCESS;
    }
}
