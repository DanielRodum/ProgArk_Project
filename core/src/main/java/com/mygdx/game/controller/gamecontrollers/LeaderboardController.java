package com.mygdx.game.controller.gamecontrollers;

import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.view.gameviews.LeaderboardView;


public class LeaderboardController {
    private final doodleMain game;
    private final GameLogic logic;
    private final String lobbyCode;
    private final FirebaseInterface firebase;
    private final LeaderboardView leaderboardView;

    public LeaderboardController(doodleMain game, LeaderboardView leaderboardView, GameLogic logic, String lobbyCode) {
        this.game = game;
        this.leaderboardView = leaderboardView;
        this.logic = logic;
        this.lobbyCode = lobbyCode;
        this.firebase = game.getFirebaseService();
        leaderboardView.updateLeaderboard(logic.getPlayers());
        game.setScreen(leaderboardView);
    }


    public void refreshLeaderboard() {
        leaderboardView.updateLeaderboard(logic.getPlayers());
    }
}
