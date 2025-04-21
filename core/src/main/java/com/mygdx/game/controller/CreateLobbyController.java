package com.mygdx.game.controller;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.view.CreateLobbyView;

public class CreateLobbyController {
    private final doodleMain game;
    private final CreateLobbyView view;
    private final FirebaseInterface firebase;
    private String lobbyCode;
    private boolean gameStarting = false;

    public CreateLobbyController(doodleMain game, CreateLobbyView view) {
        this.game     = game;
        this.view     = view;
        this.firebase = game.getFirebaseService();
        initialize();
    }

    private void initialize() {
        view.showStatus("Creating lobby…");
        firebase.createLobby(game.getPlayerName(), new FirebaseInterface.LobbyCallback() {
            @Override public void onSuccess(String code) {
                Gdx.app.postRunnable(() -> {
                    lobbyCode = code;
                    view.setLobbyCode(code);
                    view.addPlayer(game.getPlayerName() + " (Host)");
                    setupListeners();
                });
            }
            @Override public void onFailure(String error) {
                Gdx.app.postRunnable(() -> {
                    view.showError("Create failed: " + error);
                    view.returnToMainMenu();
                });
            }
        });
    }

    private void setupListeners() {
        firebase.setupLobbyListener(lobbyCode, new FirebaseInterface.LobbyStateCallback() {
            @Override public void onPlayerJoined(String n) { Gdx.app.postRunnable(() -> view.addPlayer(n)); }
            @Override public void onPlayerLeft(String n)   { Gdx.app.postRunnable(() -> view.removePlayer(n)); }
            @Override public void onGameStarted(String d)  { gameStarting = true; Gdx.app.postRunnable(() -> view.showStatus("Game starting…")); }
            @Override public void onLobbyClosed()          { Gdx.app.postRunnable(() -> view.returnToMainMenu()); }
            @Override public void onError(String m)        { Gdx.app.postRunnable(() -> view.showError("Error: " + m)); }
        });
    }

    public void handleStartGame() {
        if (view.getPlayerCount() < 2) {
            view.showError("Need at least 2 players");
            return;
        }
        firebase.startGame(lobbyCode, game.getPlayerName());
    }

    public void leaveLobby() {
        if (!gameStarting) {
            firebase.leaveLobby(lobbyCode, game.getPlayerName());
            view.returnToMainMenu();
        }
    }
}
