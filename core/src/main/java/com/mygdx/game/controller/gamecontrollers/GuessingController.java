package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.utils.RoundTimer;
import com.mygdx.game.view.gameviews.GuessingView;
import com.mygdx.game.view.gameviews.LeaderboardView;

public class GuessingController {
    private final doodleMain game;
    private final GameLogic gameLogic;
    private final GuessingView guessingView;
    private final String lobbyCode;
    private final FirebaseInterface firebase;
    private RoundTimer timer;

    public GuessingController(doodleMain game,
                              GameLogic gameLogic,
                              GuessingView guessingView,
                              String lobbyCode) {
        this.game         = game;
        this.gameLogic    = gameLogic;
        this.guessingView = guessingView;
        this.lobbyCode    = lobbyCode;
        this.firebase     = game.getFirebaseService();

        // Subscribe to live drawing strokes
        firebase.subscribeToStrokes(lobbyCode, (strokeId, points, colorHex) ->
            Gdx.app.postRunnable(() -> guessingView.addRemoteStroke(points, Color.valueOf(colorHex)))
        );

        // Fetch the chosen word once and display masked
        firebase.getChosenWord(lobbyCode, new FirebaseInterface.WordCallback() {
            @Override public void onSuccess(String word) {
                gameLogic.setCurrentWord(word);
                String masked = gameLogic.getMaskedWord();
                Gdx.app.postRunnable(() -> guessingView.displayMaskedWord(masked));
            }
            @Override public void onFailure(Exception e) {
                // handle error if needed
            }
        });

        // Start the guess timer; transition only when time's up
        startGuessTimer(60);
    }

    private void startGuessTimer(int seconds) {
        timer = new RoundTimer(seconds, new RoundTimer.TimerListener() {
            @Override public void onTick(int secsLeft) {
                // optionally update a timer UI if you add one
            }
            @Override public void onTimeUp() {
                // move everyone to the leaderboard
                Gdx.app.postRunnable(() -> game.setScreen(new LeaderboardView()));
            }
        });
        timer.start();
    }

    /** Called when player submits a guess */
    public void onGuessSubmitted(String guess) {
        if (gameLogic.isCorrectGuess(guess)) {
            Gdx.app.postRunnable(() -> guessingView.showCorrectFeedback());
            // guessers simply wait here; drawing controller will schedule next round
        } else {
            Gdx.app.postRunnable(() -> guessingView.showIncorrectFeedback());
        }
    }
}
