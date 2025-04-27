package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.model.Player;
import com.mygdx.game.utils.RoundTimer;
import com.mygdx.game.view.MainMenuView;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DrawingController {
    private final doodleMain game;
    private final FirebaseInterface firebase;
    private final String lobbyCode;
    private final String currentDrawer;
    private final GameLogic logic;
    private RoundTimer roundTimer;

    // ───── New fields for cycle tracking ─────
    private final Set<String> drawnThisCycle = new LinkedHashSet<>();
    private int cyclesCompleted = 0;
    private static final int MAX_CYCLES = 5;

    public DrawingController(doodleMain game,
                             com.mygdx.game.view.gameviews.DrawingView view,
                             String lobbyCode) {
        this.game          = game;
        this.firebase      = game.getFirebaseService();
        this.lobbyCode     = lobbyCode;
        this.currentDrawer = game.getPlayerName();
        this.logic         = new GameLogic(firebase, lobbyCode);

        // 1) Fetch & display the chosen word
        firebase.getChosenWord(lobbyCode, new FirebaseInterface.WordCallback() {
            @Override public void onSuccess(String word) {
                Gdx.app.postRunnable(() -> view.setWord(word));
            }
            @Override public void onFailure(Exception e) {
                Gdx.app.postRunnable(() -> view.setWord("<Error>"));
            }
        });

        // 2) Initialize timer UI
        Gdx.app.postRunnable(() -> view.setTime(60));

        // 3) Listen for remote strokes
        firebase.subscribeToStrokes(lobbyCode, (id, pts, hex) ->
                Gdx.app.postRunnable(() -> view.addRemoteStroke(pts, com.badlogic.gdx.graphics.Color.valueOf(hex)))
        );

        // 4) When everyone’s guessed or timer expires, end round
        firebase.subscribeToGuesses(lobbyCode, this::endRound);

        // start the ticking UI timer
        firebase.subscribeToTimer(lobbyCode, (startMs, durationS) -> {
            long now = System.currentTimeMillis();
            final int secsLeft = (int)(durationS - (now - startMs) / 1000);  // ← mark final

            if (secsLeft < 0) {
                Gdx.app.postRunnable(() -> view.setTime(0));
                // immediately fire time-up if needed
                Gdx.app.postRunnable(this::endRound);
                return;
            }

            // first, show the current remaining:
            Gdx.app.postRunnable(() -> view.setTime(secsLeft));

            // then schedule ticks every second:
            Timer.schedule(new Timer.Task() {
                int remaining = secsLeft;
                @Override
                public void run() {
                    remaining--;
                    if (remaining >= 0) {
                        Gdx.app.postRunnable(() -> view.setTime(remaining));
                    } else {
                        this.cancel();
                        Gdx.app.postRunnable(DrawingController.this::endRound);
                    }
                }
            }, 1, 1, secsLeft);
        });
    }

    /** Called from the view whenever the drawer draws or drags. */
    public void onLocalStrokeCompleted(String strokeId,
                                       List<com.badlogic.gdx.math.Vector2> points,
                                       com.badlogic.gdx.graphics.Color color) {
        String hex = String.format("#%02x%02x%02x",
                (int)(color.r * 255),
                (int)(color.g * 255),
                (int)(color.b * 255)
        );
        firebase.sendStroke(lobbyCode, strokeId, points, hex);
    }

    private void endRound() {
        // stop the timer
        if (roundTimer != null && roundTimer.isRunning()) {
            roundTimer.stop();
        }

        // mark this drawer as done in this cycle
        drawnThisCycle.add(currentDrawer);

        // hand off to LeaderboardController (which shows the leaderboard)
        Gdx.app.postRunnable(() -> {
            new com.mygdx.game.controller.gamecontrollers.LeaderboardController(
                    game, logic, lobbyCode
            );
        });

        // only the drawer schedules the next round
        if (!game.getPlayerName().equals(currentDrawer)) {
            return;
        }

        // if everyone has drawn, that completes one cycle
        if (drawnThisCycle.size() >= logic.getPlayers().size()) {
            cyclesCompleted++;
            drawnThisCycle.clear();
        }

        // if we still have cycles left, pick the next drawer
        if (cyclesCompleted < MAX_CYCLES) {
            List<String> order = logic.getPlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
            int idx  = order.indexOf(currentDrawer);
            String next = order.get((idx + 1) % order.size());

            // small delay so everyone sees the leaderboard briefly
            Timer.schedule(new Timer.Task() {
                @Override public void run() {
                    firebase.startGame(lobbyCode, next);
                }
            }, 5f);

        } else {
            // max cycles reached → end game: back to main menu
            Gdx.app.postRunnable(() -> {
                game.setScreen(new MainMenuView(game));
            });
        }
    }
}
