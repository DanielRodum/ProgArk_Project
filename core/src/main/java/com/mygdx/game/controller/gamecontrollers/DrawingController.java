package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.view.gameviews.DrawingView;
import com.mygdx.game.view.gameviews.LeaderboardView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class DrawingController {
    private final doodleMain game;
    private final DrawingView view;
    private final FirebaseInterface firebase;
    private final String lobbyCode;
    private final String currentDrawer;
    private final GameLogic logic;
    private int secondsLeft = 60;
    private Timer timer;

    public DrawingController(doodleMain game, DrawingView view, String lobbyCode) {
        this.game          = game;
        this.view          = view;
        this.lobbyCode     = lobbyCode;
        this.firebase      = game.getFirebaseService();
        this.currentDrawer = game.getPlayerName();          // the drawer’s own name
        this.logic         = new GameLogic(firebase, lobbyCode);

        fetchWord();
        subscribeToRemoteStrokes();
        startTimer();
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

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override public void run() {
                Gdx.app.postRunnable(() -> {
                    secondsLeft--;
                    view.setTime(secondsLeft);

                    if (secondsLeft <= 0) {
                        timer.cancel();

                        // 1) Move everyone to the leaderboard screen
                        game.setScreen(new LeaderboardView());

                        // 2) Only the drawer schedules the next turn
                        if (game.getPlayerName().equals(currentDrawer)) {
                            // pull fresh list of player names
                            List<String> names = logic.getPlayers().stream()
                                .map(p -> p.getName())
                                .collect(Collectors.toList());

                            int idx = names.indexOf(currentDrawer);
                            // next in round‑robin
                            String nextDrawer = names.get((idx + 1) % names.size());

                            // kick off the next “choose word” phase everywhere
                            firebase.startGame(lobbyCode, nextDrawer);
                        }
                    }
                });
            }
        }, 1_000, 1_000);
    }

    public void stopTimer() {
        if (timer != null) timer.cancel();
    }
}
