package com.donglaistd.jinli.database.entity.game.goldenflower;

import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;

public interface PokerService {
    void fold(RacePokerPlayer player);

    void check(RacePokerPlayer player);

    void betChipIn(RacePokerPlayer player, int chip, boolean playerOpt);

    void endRoundOrNextTurn();

    void sendNextTurnMessage();

    boolean checkRoundEnd();

    void endGame();

    void startGame();

    void startTimer();

    void cancelTimer(boolean flag);

    boolean checkStart();

    void lookCards(RacePokerPlayer player);
}
