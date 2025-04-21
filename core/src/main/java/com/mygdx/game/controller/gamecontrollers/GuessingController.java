package com.mygdx.game.controller.gamecontrollers;

import com.mygdx.game.doodleMain;
import com.mygdx.game.view.gameviews.GuessingView;
import com.mygdx.game.model.GameLogicModel;
import com.mygdx.game.model.Guess;
import com.mygdx.game.model.Player;

public class GuessingController {
    private final doodleMain game;
    private final GuessingView view;
    private final GameLogicModel model;
    private final Player localPlayer;

    public GuessingController(doodleMain game, GuessingView view, GameLogicModel model, Player localPlayer) {
        this.game = game;
        this.view = view;
        this.model = model;
        this.localPlayer = localPlayer;
        view.displayMaskedWord(model.getMaskedWord());
    }

    /**
     * Called when the guesser submits a guess.
     */
    public void onGuessSubmitted(String guessText) {
        Guess guess = new Guess(localPlayer.getId(), guessText);
        model.addGuess(guess);
        if (guess.isCorrect()) {
            view.showCorrectFeedback();
            // TODO: notify GameLogicController to end the round or update scores
        } else {
            view.showIncorrectFeedback();
        }
    }
}
