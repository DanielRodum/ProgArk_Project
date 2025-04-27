package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.model.Player;
import com.mygdx.game.view.gameviews.GuessingView;
import com.mygdx.game.view.gameviews.LeaderboardView;

import java.util.ArrayList;
import java.util.List;

public class GuessingController implements GuessingView.GuessListener {
    private final doodleMain game;
    private final GameLogic logic;
    private final GuessingView view;
    private final String lobbyCode;
    private final FirebaseInterface firebase;
    private boolean roundEnded = false;

    public GuessingController(doodleMain game,
                              GameLogic logic,
                              GuessingView view,
                              String lobbyCode) {
        this.game       = game;
        this.logic      = logic;
        this.view       = view;
        this.lobbyCode  = lobbyCode;
        this.firebase   = game.getFirebaseService();

        initView();
        subscribeStrokes();
        subscribeGuesses();
    }

    private void initView() {
        Gdx.app.postRunnable(() -> {
            view.displayMaskedWord(logic.getMaskedWord());
            view.setTime(60);
            // wire up this controller as the GuessListener
            view.setGuessListener(this);
        });
    }

    private void subscribeStrokes() {
        firebase.subscribeToStrokes(lobbyCode, (id, pts, hex) ->
            Gdx.app.postRunnable(() ->
                view.addRemoteStroke(pts, Color.valueOf(hex))
            )
        );
    }

    private void subscribeGuesses() {
        firebase.subscribeToGuesses(lobbyCode, () -> endRound());
    }

    @Override
    public void onGuess(String guess) {
        if (roundEnded) return;
        if (logic.isCorrectGuess(guess)) {
            firebase.recordGuess(lobbyCode, game.getPlayerName());
            firebase.updatePlayerScore(lobbyCode, game.getPlayerName(), 100);
            Gdx.app.postRunnable(() -> view.showCorrectFeedback());
        } else {
            Gdx.app.postRunnable(() -> view.showIncorrectFeedback());
        }
    }

    private void endRound() {
        if (roundEnded) return;
        roundEnded = true;
        Gdx.app.postRunnable(() -> {
            String drawer = logic.getCurrentDrawer();
            LeaderboardView lbv = new LeaderboardView(game, lobbyCode, drawer);
            new LeaderboardController(game, logic, lobbyCode);
            game.setScreen(lbv);
        });
    }
}
