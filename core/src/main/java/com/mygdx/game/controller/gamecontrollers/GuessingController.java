package com.mygdx.game.controller.gamecontrollers;

import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.model.Player;
import com.mygdx.game.view.gameviews.GuessingView;

public class GuessingController {

    private final doodleMain game;

    private GameLogic gameLogic;

    private GuessingView guessingView;

    private Player player;

    public GuessingController(doodleMain game, GameLogic gameLogic, GuessingView guessingView, Player player) {
        this.game = game;
        this.gameLogic = gameLogic;
        this.guessingView = guessingView;
        this.player = player;
    }

    public void onGuessSubmitted(String guess) {
        if (guess == null || guess.trim().isEmpty()) {
            return;
        }

        String correctWord = gameLogic.getCurrentWord();

        gameLogic.guessWord(player, guess);

        if (guess.trim().equalsIgnoreCase(correctWord)) {
            guessingView.showCorrectFeedback();
        } else {
            guessingView.showIncorrectFeedback();
        }
    }
}
