package com.donglaistd.jinli.processors.handler.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.BullBullBuilder;
import com.donglaistd.jinli.database.entity.game.BullBull;
import com.donglaistd.jinli.processors.handler.SwitchGameRequestHandler;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.donglaistd.jinli.Constant.GameType.BACCARAT;


public class PauseGameHandlerTest extends BaseTest {

    @Autowired
    BullBullBuilder bullBullBuilder;
    @Autowired
    PauseGameRequestHandler pauseGameRequestHandler;
    @Autowired
    private SwitchGameRequestHandler switchGameRequestHandler;

    @Test

    public void pauseAndResumeGameTest() throws InterruptedException {
        var gameBuilder = Game.GameRequest.newBuilder();
        var pauseGameRequest = Game.PauseGameRequest.newBuilder();
        gameBuilder.setPauseGameRequest(pauseGameRequest);
        BullBull bullBull = bullBullBuilder.create(true);
        liveUser.setPlayingGameId(bullBull.getGameId());
        dataManager.saveLiveUser(liveUser);
        bullBull.setOwner(liveUser);
        bullBull.beginGameLoop(800);
        bullBull.setDelayFinishTime(300);
        DataManager.addGame(bullBull);
        pauseGameRequestHandler.handle(context, gameBuilder.build(), bullBull);
        Assert.assertEquals(Constant.GameStatus.PAUSED, bullBull.getNextGameStatue());
        Thread.sleep(1500);
        Assert.assertEquals(Constant.GameStatus.PAUSED, bullBull.getGameStatus());
        bullBull.resumeGame();
        Assert.assertEquals(Constant.GameStatus.BETTING, bullBull.getGameStatus());
        Assert.assertEquals(Constant.GameStatus.IDLE, bullBull.getNextGameStatue());
        forceStop(bullBull);
    }

    @Test

    public void illegalPauseGame() throws InterruptedException {
        var gameBuilder = Game.GameRequest.newBuilder();
        var pauseGameRequest = Game.PauseGameRequest.newBuilder();
        gameBuilder.setPauseGameRequest(pauseGameRequest);
        BullBull bullBull = bullBullBuilder.create(true);
        liveUser.setPlayingGameId(bullBull.getGameId());
        dataManager.saveLiveUser(liveUser);
        bullBull.setOwner(liveUser);
        bullBull.beginGameLoop(800);
        bullBull.setDelayFinishTime(300);
        DataManager.addGame(bullBull);
        //switch game after to pause game
        var request = Jinli.JinliMessageRequest.newBuilder();
        var switchGameRequest = Jinli.SwitchGameRequest.newBuilder();
        switchGameRequest.setGameType(BACCARAT);
        request.setSwitchGameRequest(switchGameRequest);
        switchGameRequestHandler.handle(context, request.build());
        var result = pauseGameRequestHandler.handle(context, gameBuilder.build(), bullBull);
        Assert.assertEquals(Constant.ResultCode.GAME_SWITCHING, result.getRight());
        Thread.sleep(200);
        Assert.assertEquals(Constant.GameStatus.ENDED, bullBull.getNextGameStatue());
    }
}
