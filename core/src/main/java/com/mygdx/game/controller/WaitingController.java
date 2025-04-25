package com.mygdx.game.controller;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.controller.gamecontrollers.GuessingController;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogic;
import com.mygdx.game.view.gameviews.ChooseWordView;
import com.mygdx.game.view.WaitingView;
import com.mygdx.game.view.MainMenuView;
import com.mygdx.game.view.gameviews.GuessingView;

public class WaitingController {
    private final doodleMain game;
    private final WaitingView view;
    private final String lobbyCode;
    private final FirebaseInterface firebase;
    private final boolean isHost;
    private String currentDrawer; // track who's drawing this round

    public WaitingController(doodleMain game, WaitingView view,
                             String lobbyCode, boolean isHost) {
        this.game      = game;
        this.view      = view;
        this.lobbyCode = lobbyCode;
        this.firebase  = game.getFirebaseService();
        this.isHost    = isHost;
        setupListeners();
    }

    private void setupListeners() {
        firebase.setupLobbyListener(lobbyCode, new FirebaseInterface.LobbyStateCallback() {
            @Override public void onPlayerJoined(String n)   { Gdx.app.postRunnable(() -> view.addPlayer(n)); }
            @Override public void onPlayerLeft(String n)     { Gdx.app.postRunnable(() -> view.removePlayer(n)); }

            @Override
            public void onGameStarted(String drawerName) {
                // remember who's drawing this round
                currentDrawer = drawerName;
                if (game.getPlayerName().equals(drawerName)) {
                    // drawer picks a word
                    game.setScreen(new ChooseWordView(game, lobbyCode));
                } else {
                    // guessers wait while drawer chooses
                    view.showStatus(drawerName + " is choosing a word…");
                }
            }

            @Override
            public void onWordChosen(String word) {
                if (currentDrawer == null) return;
                Gdx.app.postRunnable(() -> {
                    // only guessers transition to guessing view
                    if (!game.getPlayerName().equals(currentDrawer)) {
                        GuessingView gv = new GuessingView(game);
                        GuessingController gc = new GuessingController(game,
                            new GameLogic(firebase, lobbyCode), gv, lobbyCode);
                        gv.setController(gc);
                        game.setScreen(gv);
                    }
                });
            }

            @Override public void onLobbyClosed()          { Gdx.app.postRunnable(() -> game.setScreen(new MainMenuView(game))); }
            @Override public void onError(String msg)    { Gdx.app.postRunnable(() -> view.showError(msg)); }
        });
    }

    public void handleStartGame() {
        if (!isHost) return;
        game.setScreen(new ChooseWordView(game, lobbyCode));
    }

    public void leaveLobby() {
        firebase.leaveLobby(lobbyCode, game.getPlayerName());
        Gdx.app.postRunnable(() -> game.setScreen(new MainMenuView(game)));
    }
}
