package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.utils.RoundTimer;
import com.mygdx.game.view.gameviews.GuessingView;
import com.mygdx.game.view.gameviews.LeaderboardView;

public class GuessingController implements GuessingView.GuessListener {
    private final doodleMain game;
    private final GameLogic logic;
    private final GuessingView view;
    private final String lobbyCode;
    private final FirebaseInterface firebase;

    private RoundTimer roundTimer;
    private boolean roundEnded = false;
    private boolean hasGuessedCorrectly = false;

    public GuessingController(doodleMain game,
                              GameLogic logic,
                              GuessingView view,
                              String lobbyCode) {
        this.game      = game;
        this.logic     = logic;
        this.view      = view;
        this.lobbyCode = lobbyCode;
        this.firebase  = game.getFirebaseService();

        initView();
        subscribeStrokes();
        subscribeGuesses();
        startTimer(60);
    }

    private void initView() {
        Gdx.app.postRunnable(() -> {
            view.displayMaskedWord(logic.getMaskedWord());
            view.setTime(60);
            view.setGuessListener(this);
        });
    }

    private void startTimer(int seconds) {
        roundTimer = new RoundTimer(seconds, new RoundTimer.TimerListener() {
            @Override public void onTick(int secsLeft) {
                Gdx.app.postRunnable(() -> view.setTime(secsLeft));
            }
            @Override public void onTimeUp() {
                endRound();
            }
        });
        roundTimer.start();
    }

    private void subscribeStrokes() {
        firebase.subscribeToStrokes(lobbyCode, (id, pts, hex) ->
            Gdx.app.postRunnable(() ->
                view.addRemoteStroke(pts, Color.valueOf(hex))
            )
        );
    }

    private void subscribeGuesses() {
        firebase.subscribeToGuesses(lobbyCode, this::endRound);
    }

    @Override
    public void onGuess(String guess) {
        if (roundEnded || hasGuessedCorrectly) return;

        if (logic.isCorrectGuess(guess)) {
            hasGuessedCorrectly = true;
            int timeLeft = roundTimer.getTimeRemaining();
            int points   = timeLeft * 3;
            firebase.recordGuess(lobbyCode, game.getPlayerName());
            firebase.updatePlayerScore(lobbyCode, game.getPlayerName(), points);
            Gdx.app.postRunnable(() -> {
                view.showCorrectFeedback();
                view.disableInput();
            });
        } else {
            Gdx.app.postRunnable(() -> view.showIncorrectFeedback());
        }
    }

    private void endRound() {
        if (roundEnded) return;
        roundEnded = true;

        if (roundTimer != null && roundTimer.isRunning()) {
            roundTimer.stop();
        }

        Gdx.app.postRunnable(() -> {
            String drawer = logic.getCurrentDrawer();
            LeaderboardView lbv = new LeaderboardView(game, lobbyCode, drawer);
            new LeaderboardController(game, logic, lobbyCode);
            game.setScreen(lbv);
        });
    }
}
