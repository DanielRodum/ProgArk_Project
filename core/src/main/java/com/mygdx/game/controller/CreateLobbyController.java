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
    private final boolean isHost;
    private boolean gameStarting = false;

    public CreateLobbyController(doodleMain game, CreateLobbyView view, String lobbyCode) {
        this.game = game;
        this.view = view;
        this.firebase = game.getFirebaseService();
        this.lobbyCode = lobbyCode;
        this.isHost = true;

        initializeLobby();
    }

    private void initializeLobby() {
        view.showStatus("Creating lobby...");
        firebase.createLobby(game.getPlayerName(), new FirebaseInterface.LobbyCallback() {
            @Override
            public void onSuccess(String code) {
                Gdx.app.postRunnable(() -> {
                    lobbyCode = code;
                    view.setLobbyCode(code);
                    view.showStatus("Lobby created! Share code: " + code);
                    setupLobbyListeners();
                    view.addPlayer(game.getPlayerName() + " (Host)");
                });
            }

            @Override
            public void onFailure(String error) {
                Gdx.app.postRunnable(() -> {
                    view.showError("Lobby creation failed: " + error);
                    view.returnToMainMenu();
                });
            }
        });
    }

    private void setupLobbyListeners() {
        firebase.setupLobbyListener(lobbyCode, new FirebaseInterface.LobbyStateCallback() {
            @Override
            public void onPlayerJoined(String playerName) {
                Gdx.app.postRunnable(() -> {
                    if (!playerName.equals(game.getPlayerName())) {
                        view.addPlayer(playerName);
                        view.showStatus(playerName + " joined the lobby");
                    }
                });
            }

            @Override
            public void onGameStarted(String drawerName) {
                gameStarting = true;
                Gdx.app.postRunnable(() -> {
                    if (game.getPlayerName().equals(drawerName)) {
                        view.showStatus("You're drawing first! Loading...");
                    } else {
                        view.showStatus(drawerName + " is drawing first...");
                    }
                    // TODO: Transition to game screen when ready
                    // game.setScreen(new DrawingView(game, lobbyCode));
                });
            }

            @Override
            public void onPlayerLeft(String playerName) {
                Gdx.app.postRunnable(() -> {
                    view.removePlayer(playerName);
                    view.showStatus(playerName + " left the lobby");
                });
            }

            @Override
            public void onLobbyClosed() {
                Gdx.app.postRunnable(() -> {
                    if (!gameStarting) { // Only show if not transitioning to game
                        view.showError("Lobby closed by host");
                        view.returnToMainMenu();
                    }
                });
            }

            @Override
            public void onError(String message) {
                Gdx.app.postRunnable(() -> view.showError("Connection error: " + message));
            }
        });
    }

    public void handleStartGame() {
        if (isHost) {
            if (view.getPlayers().size < 2) {
                view.showError("Need at least 2 players to start");
                return;
            }
            view.showStatus("Starting game...");
            firebase.startGame(lobbyCode, game.getPlayerName());
        }
    }

    public void leaveLobby() {
        if (!gameStarting) { // Prevent double leave
            firebase.leaveLobby(lobbyCode, game.getPlayerName());
            view.returnToMainMenu();
        }
    }
}
