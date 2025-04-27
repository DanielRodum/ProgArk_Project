package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.model.Player;
import com.mygdx.game.view.gameviews.LeaderboardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaderboardController {
    private final doodleMain game;
    private final GameLogic logic;
    private final String lobbyCode;
    private final FirebaseInterface firebase;

    public LeaderboardController(doodleMain game, GameLogic logic, String lobbyCode) {
        this.game = game;
        this.logic = logic;
        this.lobbyCode = lobbyCode;
        this.firebase = game.getFirebaseService();

        // Show initial leaderboard
        showLeaderboard();

        // Live updates for scores
        subscribeScoreUpdates();

        // Listen for the next round start
        subscribeRoundStart();
    }

    private void showLeaderboard() {
        Gdx.app.postRunnable(() -> {
            String drawer = logic.getCurrentDrawer();
            LeaderboardView view = new LeaderboardView(game, lobbyCode, drawer);
            view.updateLeaderboard(logic.getPlayers());
            game.setScreen(view);
        });
    }

    private void subscribeScoreUpdates() {
        firebase.subscribeToScores(lobbyCode, scoresMap -> {
            List<Player> updated = new ArrayList<>(logic.getPlayers());
            for (Player p : updated) {
                p.setScore(scoresMap.getOrDefault(p.getName(), 0));
            }
            Gdx.app.postRunnable(() -> {
                if (game.getScreen() instanceof LeaderboardView) {
                    ((LeaderboardView) game.getScreen()).updateLeaderboard(updated);
                }
            });
        });
    }

    private void subscribeRoundStart() {
        firebase.setupLobbyListener(lobbyCode, new FirebaseInterface.LobbyStateCallback() {
            @Override public void onGameStarted(String drawerName) {
                // when a new round starts, re-show leaderboard before transition
                showLeaderboard();
            }
            @Override public void onPlayerJoined(String name) {}
            @Override public void onPlayerLeft(String name) {}
            @Override public void onWordChosen(String word) {}
            @Override public void onLobbyClosed() {}
            @Override public void onError(String msg) {}
        });
    }
}
