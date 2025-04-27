package com.mygdx.game.controller;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.controller.gamecontrollers.GuessingController;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.view.MainMenuView;
import com.mygdx.game.view.WaitingView;
import com.mygdx.game.view.gameviews.ChooseWordView;
import com.mygdx.game.view.gameviews.GuessingView;

public class WaitingController {
    private final doodleMain game;
    private final WaitingView view;
    private final String lobbyCode;
    private final boolean isHost;
    private final FirebaseInterface firebase;
    private final GameLogic logic;

    public WaitingController(doodleMain game, WaitingView view,
                             String lobbyCode, boolean isHost) {
        this.game      = game;
        this.view      = view;
        this.lobbyCode = lobbyCode;
        this.isHost    = isHost;
        this.firebase  = game.getFirebaseService();
        this.logic     = new GameLogic(firebase, lobbyCode);

        setupListeners();
    }

    private void setupListeners() {
        firebase.setupLobbyListener(lobbyCode, new FirebaseInterface.LobbyStateCallback() {
            @Override public void onPlayerJoined(String name) {
                Gdx.app.postRunnable(() -> view.addPlayer(name));
            }
            @Override public void onPlayerLeft(String name) {
                Gdx.app.postRunnable(() -> view.removePlayer(name));
            }

            @Override public void onGameStarted(String drawerName) {
                String drawer = logic.getCurrentDrawer();
                if (game.getPlayerName().equals(drawer)) {
                    Gdx.app.postRunnable(() ->
                        game.setScreen(new ChooseWordView(game, lobbyCode))
                    );
                } else {
                    Gdx.app.postRunnable(() ->
                        view.showStatus(drawer + " is choosing a word…")
                    );
                }
            }

            @Override public void onWordChosen(String word) {
                String drawer = logic.getCurrentDrawer();
                if (!game.getPlayerName().equals(drawer)) {
                    Gdx.app.postRunnable(() -> {
                        GuessingView guessingView = new GuessingView(game);
                        GuessingController gc = new GuessingController(
                            game,
                            logic,
                            guessingView,
                            lobbyCode
                        );
                        guessingView.setGuessListener(gc);
                        game.setScreen(guessingView);
                    });
                }
            }

            @Override public void onLobbyClosed() {
                Gdx.app.postRunnable(() -> game.setScreen(new MainMenuView(game)));
            }
            @Override public void onError(String msg) {
                Gdx.app.postRunnable(() -> view.showError(msg));
            }
        });
    }

    public void handleStartGame() {
        if (!isHost) return;
        // replaced view.getPlayerCount() with the correct call to your WaitingView
        if (view.getPlayers().size() < 2) {
            view.showError("Need at least 2 players");
            return;
        }
        game.setScreen(new ChooseWordView(game, lobbyCode));
    }

    public void leaveLobby() {
        firebase.leaveLobby(lobbyCode, game.getPlayerName());
        Gdx.app.postRunnable(() -> game.setScreen(new MainMenuView(game)));
    }
}
