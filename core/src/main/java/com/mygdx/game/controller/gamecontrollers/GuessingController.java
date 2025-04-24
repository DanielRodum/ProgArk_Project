package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.model.Player;
import com.mygdx.game.utils.RoundTimer;
import com.mygdx.game.view.gameviews.GuessingView;
import com.mygdx.game.view.gameviews.LeaderboardView;

public class GuessingController {
    private final doodleMain game;
    private final GameLogic logic;
    private final GuessingView guessingView;
    private final String lobbyCode;
    private final FirebaseInterface firebase;
    private RoundTimer timer;

    public GuessingController(doodleMain game,
                              GameLogic logic,
                              GuessingView guessingView,
                              String lobbyCode) {
        this.game = game;
        this.logic = logic;
        this.guessingView = guessingView;
        this.lobbyCode = lobbyCode;
        this.firebase = game.getFirebaseService();

        // show masked word
        guessingView.displayMaskedWord(logic.getMaskedWord());

        firebase.subscribeToStrokes(lobbyCode, (strokeId, points, colorHex) ->
            Gdx.app.postRunnable(() -> guessingView.addRemoteStroke(points, Color.valueOf(colorHex)))
        );

        // subscribe to guess completions
        firebase.subscribeToGuesses(lobbyCode, new FirebaseInterface.GuessesCallback() {
            @Override
            public void onAllGuessed() {
                endRound();
            }
        });

        // start countdown
        startTimer(60);
    }

    private void startTimer(int seconds) {
        timer = new RoundTimer(seconds, new RoundTimer.TimerListener() {
            @Override public void onTick(int secsLeft) {
                Gdx.app.postRunnable(() -> guessingView.setTime(secsLeft));
            }
            @Override public void onTimeUp() {
                endRound();
            }
        });
        timer.start();
    }

    public void onGuessSubmitted(String guess) {
        if (logic.isCorrectGuess(guess)) {
            firebase.recordGuess(lobbyCode, game.getPlayerName());

            int timeLeft = timer != null ? timer.getTimeRemaining() : 0;
            //int score = Math.max(0, 100 - timeLeft);
            int score = timeLeft*3;

            firebase.updatePlayerScore(lobbyCode, game.getPlayerName(), score);

            //for (Player player : logic.getPlayers()) {
            //    if (player.getName().equals(game.getPlayerName())) {
            //       player.addScore(score);
            //        break;
            //    }
            //}

            Gdx.app.postRunnable(() -> guessingView.showCorrectFeedback());
        } else {
            Gdx.app.postRunnable(() -> guessingView.showIncorrectFeedback());
        }
    }

    private void endRound() {
        if (timer != null && timer.isRunning()) timer.stop();
        Gdx.app.postRunnable(() -> new LeaderboardController(game, new LeaderboardView(), logic, lobbyCode));
    }
}
