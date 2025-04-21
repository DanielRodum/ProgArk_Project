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
                    view.showError("Could not create: " + error);
                    view.returnToMainMenu();
                });
            }
        });
    }

    private void setupListeners() {
        firebase.setupLobbyListener(lobbyCode, new FirebaseInterface.LobbyStateCallback() {
            @Override public void onPlayerJoined(String name) {
                Gdx.app.postRunnable(() -> view.addPlayer(name));
            }
            @Override public void onPlayerLeft(String name) {
                Gdx.app.postRunnable(() -> view.removePlayer(name));
            }
            @Override public void onGameStarted(String drawer) {
                gameStarting = true;
                Gdx.app.postRunnable(() -> {
                    // TODO: switch to DrawingView/GuessingView
                    view.showStatus("Game starting…");
                });
            }
            @Override public void onLobbyClosed() {
                Gdx.app.postRunnable(() -> view.returnToMainMenu());
            }
            @Override public void onError(String msg) {
                Gdx.app.postRunnable(() -> view.showError("Error: " + msg));
            }
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
