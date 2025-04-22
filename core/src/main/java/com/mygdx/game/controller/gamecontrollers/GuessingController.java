package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.view.gameviews.GuessingView;

public class GuessingController {
    private final doodleMain game;
    private final GameLogic gameLogic;
    private final GuessingView guessingView;
    private final String lobbyCode;
    private final FirebaseInterface firebase;

    public GuessingController(doodleMain game,
                              GameLogic gameLogic,
                              GuessingView guessingView,
                              String lobbyCode) {
        this.game         = game;
        this.gameLogic    = gameLogic;
        this.guessingView = guessingView;
        this.lobbyCode    = lobbyCode;
        this.firebase     = game.getFirebaseService();
        subscribeToWord();
    }

    /** Hører etter tegnerens valgte ord */
    private void subscribeToWord() {
        firebase.setupLobbyListener(lobbyCode, new FirebaseInterface.LobbyStateCallback() {
            @Override public void onPlayerJoined(String n)       {}
            @Override public void onPlayerLeft(String n)         {}
            @Override public void onGameStarted(String d)        {}
            @Override public void onWordChosen(String word) {
                gameLogic.setCurrentWord(word);
                String masked = gameLogic.getMaskedWord();
                Gdx.app.postRunnable(() -> guessingView.displayMaskedWord(masked));
            }
            @Override public void onLobbyClosed()                {}
            @Override public void onError(String message)        {}
        });
    }

    /** Kalles når spiller trykker på "Guess" i GuessingView */
    public void onGuessSubmitted(String guess) {
        if (guess == null || guess.trim().isEmpty()) return;
        boolean correct = guess.equalsIgnoreCase(gameLogic.getCurrentWord());
        Gdx.app.postRunnable(() -> {
            if (correct) guessingView.showCorrectFeedback();
            else         guessingView.showIncorrectFeedback();
        });
    }
}
