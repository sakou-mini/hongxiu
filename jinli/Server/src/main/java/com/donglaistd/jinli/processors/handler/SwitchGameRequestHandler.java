package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.annotation.IgnoreShutDown;
import com.donglaistd.jinli.config.SpringContext;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.database.entity.game.EmptyGame;
import com.donglaistd.jinli.event.GameEndEvent;
import com.donglaistd.jinli.listener.SwitchGameListener;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.VerifyUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@IgnoreShutDown
@Component
public class SwitchGameRequestHandler extends MessageHandler {

    public static final Map<BaseGame, SwitchGameListener> gameListenerMap = new HashMap<>();

    @Autowired
    SwitchGameListener switchGameListener;

    @Autowired
    DataManager dataManager;
    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        switchGameListener = SpringContext.getBean(SwitchGameListener.class);
        var request = messageRequest.getSwitchGameRequest();
        var reply = Jinli.SwitchGameReply.newBuilder();
        var liveUser = dataManager.findLiveUser(user.getLiveUserId());
        if (liveUser == null || liveUser.getRoomId() == null) {
            return buildReply(reply, NOT_LIVE_USER);
        }
        var playingGame = (BaseGame) DataManager.findGame(liveUser.getPlayingGameId());
        if (playingGame == null) {
            return buildReply(reply, NOT_GAME_OWNER);
        }
        if (VerifyUtil.verifyIsEmptyGameType(request.getGameType()) || playingGame instanceof EmptyGame) {
            return buildReply(reply, CANNOT_START_EMPTY_GAME);
        }
        playingGame.endGame();
        switchGameListener.setExpectingGame(playingGame);
        switchGameListener.setNewGameType(request.getGameType());
        switchGameListener.setOpenBanker(request.getIsBankerGame());
        var listener = gameListenerMap.get(playingGame);
        if (Objects.nonNull(listener)) {
            EventPublisher.removeListener(GameEndEvent.class, listener);
        }
        gameListenerMap.put(playingGame, switchGameListener);
        EventPublisher.addListener(GameEndEvent.class, this.switchGameListener);
        broadcastSwitchGameStart(liveUser,request.getGameType());
        return buildReply(reply, SUCCESS);
    }

    public void broadcastSwitchGameStart(LiveUser liveUser, Constant.GameType switchGameType){
        Jinli.SwitchGameBroadcast.Builder builder = Jinli.SwitchGameBroadcast.newBuilder().setGameType(switchGameType);
        Room room = DataManager.findOnlineRoom(liveUser.getRoomId());
        if(Objects.nonNull(room)) {
            room.broadCastToAllPlatform(buildReply(builder));
        }
    }
}
