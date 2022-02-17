package com.donglaistd.jinli.processors.handler.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;

public abstract class GameHandler {
    abstract public Pair<Game.GameReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, Game.GameRequest messageRequest, BaseGame game);
}
