package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.utils.RoundTimer;
import com.mygdx.game.view.gameviews.DrawingView;
import com.mygdx.game.view.gameviews.LeaderboardView;

import java.util.List;
import java.util.stream.Collectors;

public class DrawingController {
    private final doodleMain game;
    private final DrawingView view;
    private final FirebaseInterface firebase;
    private final String lobbyCode;
    private final String currentDrawer;
    private final GameLogic logic;
    private RoundTimer roundTimer;

    public DrawingController(doodleMain game, DrawingView view, String lobbyCode) {
        this.game          = game;
        this.view          = view;
        this.lobbyCode     = lobbyCode;
        this.firebase      = game.getFirebaseService();
        this.currentDrawer = game.getPlayerName();          // the drawer’s own name
        this.logic         = new GameLogic(firebase, lobbyCode);

        fetchWord();
        view.setTime(60);
        subscribeToRemoteStrokes();

        firebase.subscribeToGuesses(lobbyCode, this::endRound);

        startRoundTimer(60);
    }

    private void startRoundTimer(int seconds) {
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

    private void endRound() {
        if (roundTimer != null && roundTimer.isRunning()) {
            roundTimer.stop();
        }
        Gdx.app.postRunnable(() -> new LeaderboardController(game, new LeaderboardView(), logic, lobbyCode));
        if (game.getPlayerName().equals(currentDrawer)) {
            // pick next drawer (round-robin)
            List<String> names = logic.getPlayers().stream()
                .map(p -> p.getName()).collect(Collectors.toList());
            int idx = names.indexOf(currentDrawer);
            String next = names.get((idx + 1) % names.size());
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    firebase.startGame(lobbyCode, next);
                }
            }, 5);
        }
    }

    private void fetchWord() {
        firebase.getChosenWord(lobbyCode, new FirebaseInterface.WordCallback() {
            @Override public void onSuccess(String word) {
                view.setWord(word);
            }
            @Override public void onFailure(Exception e) {
                view.setWord("<Error>");
            }
        });
    }

    public void onLocalStrokeCompleted(String strokeId, List<Vector2> points, Color color) {
        // Convert the color to a hex string:
        String hex = String.format("#%02x%02x%02x",
            (int)(color.r * 255),
            (int)(color.g * 255),
            (int)(color.b * 255)
        );
        firebase.sendStroke(lobbyCode, strokeId, points, hex);
    }

    private void subscribeToRemoteStrokes() {
        firebase.subscribeToStrokes(lobbyCode, (strokeId, points, colorHex) ->
            Gdx.app.postRunnable(() -> {
                Color c = Color.valueOf(colorHex);
                view.addRemoteStroke(points, Color.valueOf(colorHex));
            })
        );
    }
}
