package com.mygdx.game.controller;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.view.WaitingView;
import com.mygdx.game.view.MainMenuView;

public class WaitingController {
    private final doodleMain game;
    private final WaitingView view;
    private final String lobbyCode;
    private final FirebaseInterface firebase;
    private final boolean isHost;

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
            @Override public void onPlayerJoined(String n) { Gdx.app.postRunnable(() -> view.addPlayer(n)); }
            @Override public void onPlayerLeft(String n)   { Gdx.app.postRunnable(() -> view.removePlayer(n)); }
            @Override public void onGameStarted(String d)  { Gdx.app.postRunnable(() -> view.onGameStarted(d)); }
            @Override public void onLobbyClosed()          { Gdx.app.postRunnable(() -> game.setScreen(new MainMenuView(game))); }
            @Override public void onError(String m)        { /* ignore or log */ }
        });
    }

    public void handleStartGame() {
        if (isHost) firebase.startGame(lobbyCode, game.getPlayerName());
    }

    public void leaveLobby() {
        firebase.leaveLobby(lobbyCode, game.getPlayerName());
        Gdx.app.postRunnable(() -> game.setScreen(new MainMenuView(game)));
    }
}
