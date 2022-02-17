package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.processors.handler.game.GameHandler;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class GameRequestHandler extends MessageHandler implements ApplicationContextAware {

    private static final Logger logger = Logger.getLogger(GameRequestHandler.class.getName());

    private final Map<String, GameHandler> messageHandlerMap = new HashMap<>();

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var request = messageRequest.getGameRequest();
        var gameMessageHandler = messageHandlerMap.get(request.getRequestCase().toString());
        var gameId = request.getGameId();
        var game = DataManager.findGame(gameId);
        if (game == null) {
            logger.warning("game is null for request" +gameMessageHandler.getClass());
            return buildReply(Game.GameReply.newBuilder(), Constant.ResultCode.GAME_NOT_EXISTS);
        }
        var pair = gameMessageHandler.handle(ctx, messageRequest.getGameRequest(), (BaseGame) game);
        return buildReply(pair.getLeft(), pair.getRight());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        var beans = applicationContext.getBeansOfType(GameHandler.class);
        for (var beanPair : beans.entrySet()) {
            messageHandlerMap.put(beanPair.getKey().replace("Handler", "").toUpperCase(), beanPair.getValue());
        }
    }
}
