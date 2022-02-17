package com.donglaistd.jinli.processors.handler.race;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.RaceGame;
import com.donglaistd.jinli.database.entity.game.goldenflower.FriedGoldenFlower;
import com.donglaistd.jinli.database.entity.game.landlord.Landlords;
import com.donglaistd.jinli.database.entity.game.texas.Texas;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.LandlordRaceProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import com.donglaistd.jinli.util.TexasUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.GAME_NOT_EXISTS;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryJoinedRaceGameInfoRequestHandler extends MessageHandler {
    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryJoinedRaceGameInfoRequest request = messageRequest.getQueryJoinedRaceGameInfoRequest();
        String gameId = request.getGameId();
        Pair<Jinli.QueryJoinedRaceGameInfoReply.Builder, Constant.ResultCode> pair = process(gameId, user);
        return buildReply(pair.getLeft(), pair.getRight());
    }

    protected Pair<Jinli.QueryJoinedRaceGameInfoReply.Builder, Constant.ResultCode> process(String gameId, User user) {
        Jinli.QueryJoinedRaceGameInfoReply.Builder reply = Jinli.QueryJoinedRaceGameInfoReply.newBuilder().setUserId(user.getId());
        var userRace = DataManager.findUserRace(user.getId());
        var raceGame = DataManager.findGame(gameId);
        if (!(raceGame instanceof RaceGame) || !userRace.getGameId().equals(gameId))
            return new Pair<>(reply, GAME_NOT_EXISTS);
        switch (raceGame.getGameType()) {
            case LANDLORD_GAME:
                var landlords = (Landlords) raceGame;
                Jinli.UserLandlordsGame raceGameInfo = LandlordRaceProcess.queryUserRaceGameInfo(landlords, user);
                reply.setUserLandlordsGame(raceGameInfo);
                return new Pair<>(reply, SUCCESS);
            case TEXAS_GAME:
                Texas texas = (Texas) raceGame;
                reply.setUserTexasGame(TexasUtil.buildUserRaceGameInfo(texas, user));
                return new Pair<>(reply, SUCCESS);

            case GOLDENFLOWER:
                FriedGoldenFlower goldenFlower = (FriedGoldenFlower) raceGame;
                reply.setUserGoldenFlowerGame(goldenFlower.buildGoldenFlowerGameInfo(user.getId()));
                return new Pair<>(reply, SUCCESS);
        }
        return null;
    }
}
