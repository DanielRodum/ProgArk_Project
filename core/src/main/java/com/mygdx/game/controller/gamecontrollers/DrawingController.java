// DrawingController.java
package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.utils.RoundTimer;
import com.mygdx.game.view.gameviews.DrawingView;
import com.mygdx.game.view.gameviews.LeaderboardView;
import com.mygdx.game.view.gameviews.ChooseWordView;

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
    private RoundTimer roundTimer;

    public DrawingController(doodleMain game, DrawingView view, String lobbyCode) {
        this.game          = game;
        this.view          = view;
        this.lobbyCode     = lobbyCode;
        this.firebase      = game.getFirebaseService();
        this.currentDrawer = game.getPlayerName();
        this.logic         = new GameLogic(firebase, lobbyCode);

        // show the chosen word
        firebase.getChosenWord(lobbyCode, new FirebaseInterface.WordCallback() {
            @Override public void onSuccess(String w) { view.setWord(w); }
            @Override public void onFailure(Exception e) { view.setWord("<Error>"); }
            });

        view.setTime(60);
            subscribeToStrokes();
            subscribeToGuesses();
            startRoundTimer(60);
        }

        private void subscribeToStrokes() {
            firebase.subscribeToStrokes(lobbyCode, (id, pts, hex) ->
                Gdx.app.postRunnable(() -> view.addRemoteStroke(pts, com.badlogic.gdx.graphics.Color.valueOf(hex)))
            );
        }

        private void subscribeToGuesses() {
            firebase.subscribeToGuesses(lobbyCode, this::endRound);
        }

        private void startRoundTimer(int seconds) {
            roundTimer = new RoundTimer(seconds, new RoundTimer.TimerListener() {
                @Override public void onTick(int s) { Gdx.app.postRunnable(() -> view.setTime(s)); }
                @Override public void onTimeUp() { endRound(); }
            });
            roundTimer.start();
        }

        private void endRound() {
            if (roundTimer != null && roundTimer.isRunning()) roundTimer.stop();

            // everyone → leaderboard
            Gdx.app.postRunnable(() -> {
                LeaderboardView lbv = new LeaderboardView();
                new LeaderboardController(game, lbv, logic, lobbyCode);
                game.setScreen(lbv);
            });

            // only current drawer schedules nextGame
            if (game.getPlayerName().equals(currentDrawer)) {
                List<String> names = logic.getPlayers().stream()
                    .map(p -> p.getName())
                    .collect(Collectors.toList());
                if (!names.isEmpty()) {
                    int idx = names.indexOf(currentDrawer);
                    if (idx < 0) idx = 0;
                    String next = names.get((idx + 1) % names.size());
                    new Timer().schedule(new TimerTask() {
                        @Override public void run() {
                            firebase.startGame(lobbyCode, next);
                        }
                    }, 2000);
                }
            }
        }

        /** Called by DrawingView when local stroke changes */
        public void onLocalStrokeCompleted(String strokeId, List<Vector2> pts, com.badlogic.gdx.graphics.Color color) {
            String hex = String.format("#%02x%02x%02x",
            (int)(color.r*255), (int)(color.g*255), (int)(color.b*255));
            firebase.sendStroke(lobbyCode, strokeId, pts, hex);
        }
    }
