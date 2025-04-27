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
import com.mygdx.game.controller.gamecontrollers.LeaderboardController;

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
        this.currentDrawer = game.getPlayerName();
        this.logic         = new GameLogic(firebase, lobbyCode);

        // 1) Fetch & display the chosen word
        firebase.getChosenWord(lobbyCode, new FirebaseInterface.WordCallback() {
            @Override
            public void onSuccess(String word) {
                Gdx.app.postRunnable(() -> view.setWord(word));
            }
            @Override
            public void onFailure(Exception e) {
                Gdx.app.postRunnable(() -> view.setWord("<Error>"));
            }
        });

        // 2) Initialize timer UI
        Gdx.app.postRunnable(() -> view.setTime(60));

        // 3) Listen for remote strokes
        firebase.subscribeToStrokes(lobbyCode, new FirebaseInterface.StrokeCallback() {
            @Override
            public void onStrokeAdded(String id, List<Vector2> pts, String colorHex) {
                Gdx.app.postRunnable(() ->
                    view.addRemoteStroke(pts, Color.valueOf(colorHex))
                );
            }
        });

        // 4) When everyone’s guessed or timer expires, end round
        firebase.subscribeToGuesses(lobbyCode, new FirebaseInterface.GuessesCallback() {
            @Override public void onAllGuessed() { endRound(); }
        });
        startRoundTimer(60);
    }

    /** Called from the view whenever the drawer draws or drags. */
    public void onLocalStrokeCompleted(String strokeId, List<Vector2> points, Color color) {
        String hex = String.format("#%02x%02x%02x",
            (int)(color.r * 255),
            (int)(color.g * 255),
            (int)(color.b * 255)
        );
        firebase.sendStroke(lobbyCode, strokeId, points, hex);
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
        // stop the timer if it’s still running
        if (roundTimer != null && roundTimer.isRunning()) {
            roundTimer.stop();
        }

        // hand off to LeaderboardController, which will show the view
        Gdx.app.postRunnable(() -> {
            new LeaderboardController(game, logic, lobbyCode);
        });

        // if I was the drawer, schedule the next drawer pick
        if (game.getPlayerName().equals(currentDrawer)) {
            List<String> names = logic.getPlayers().stream()
                .map(p -> p.getName())
                .collect(Collectors.toList());
            int idx  = names.indexOf(currentDrawer);
            String next = names.get((idx + 1) % names.size());
            Timer.schedule(new Timer.Task() {
                @Override public void run() {
                    firebase.startGame(lobbyCode, next);
                }
            }, 5f);
        }
    }
}
