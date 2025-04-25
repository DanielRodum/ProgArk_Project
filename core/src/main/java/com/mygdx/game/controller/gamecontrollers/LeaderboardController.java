package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.model.Player;
import com.mygdx.game.view.gameviews.ChooseWordView;
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

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Player drawer = logic.getPlayers().stream().filter(Player::isDrawer).findFirst().orElse(null);

                if (drawer != null && drawer.getName().equals(game.getPlayerName())){
                    game.setScreen(new ChooseWordView(game, lobbyCode));
                } else{
                    leaderboardView.displayWaitingMessage(drawer != null ? drawer.getName() : "the drawer");
                }
            }
        }, 10);
    }


    public void refreshLeaderboard() {
        leaderboardView.updateLeaderboard(logic.getPlayers());
    }
}
