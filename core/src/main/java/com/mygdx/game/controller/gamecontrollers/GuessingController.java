package com.mygdx.game.controller;

import com.mygdx.game.model.GameStateModel;
import com.mygdx.game.model.Player;
import com.mygdx.game.view.gameViews.GuessingView;

public class GuessingController {

    private GameStateModel gameState;
    private GuessingView view;
    private Player player;

    public GuessingController(GameStateModel gameState, GuessingView view, Player player) {
        this.gameState = gameState;
        this.view = view;
        this.player = player;
    }

    public void startGuessingPhase() {
        String maskedWord = gameState.getMaskedWord();
        view.displayMaskedWord(maskedWord);
    }

    public void handleGuess(String guess) {
        gameState.addGuess(player, guess, guess.equalsIgnoreCase(gameState.getCurrentWord()));
        if (guess.equalsIgnoreCase(gameState.getCurrentWord())) {
            view.showCorrectGuessFeedback();
        } else {
            view.showIncorrectGuessFeedback();
        }
    }
}
