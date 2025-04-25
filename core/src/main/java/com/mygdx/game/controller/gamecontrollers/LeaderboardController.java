// LeaderboardController.java
package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.view.gameviews.ChooseWordView;
import com.mygdx.game.view.gameviews.LeaderboardView;

public class LeaderboardController {
    private final doodleMain game;
    private final LeaderboardView leaderboardView;
    private final GameLogic logic;
    private final String lobbyCode;
    private final FirebaseInterface firebase;

    public LeaderboardController(doodleMain game,
                                 LeaderboardView leaderboardView,
                                 GameLogic logic,
                                 String lobbyCode) {
        this.game           = game;
        this.leaderboardView = leaderboardView;
        this.logic          = logic;
        this.lobbyCode      = lobbyCode;
        this.firebase       = game.getFirebaseService();

        // show initial scores
        leaderboardView.updateLeaderboard(logic.getPlayers());

        // now listen for the next round status flip
        firebase.setupLobbyListener(lobbyCode, new FirebaseInterface.LobbyStateCallback() {
            @Override public void onPlayerJoined(String n)   {}
            @Override public void onPlayerLeft(String n)     {}

            @Override
            public void onGameStarted(String drawerName) {
                Gdx.app.postRunnable(() -> {
                    if (game.getPlayerName().equals(drawerName)) {
                        // I'm the drawer → choose word
                        game.setScreen(new ChooseWordView(game, lobbyCode));
                    } else {
                        // stay on leaderboard until word chosen
                        game.setScreen(leaderboardView);
                        leaderboardView.displayWaitingMessage(drawerName);
                    }
                });
            }

            @Override public void onWordChosen(String word) {}
            @Override public void onLobbyClosed()          {}
            @Override public void onError(String msg)       {}
        });
    }
}
