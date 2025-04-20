package com.mygdx.game.controller;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.view.JoinLobbyView;

public class JoinLobbyController {
    private final doodleMain game;
    private final JoinLobbyView view;
    private final FirebaseInterface firebase;
    private final String lobbyCode;
    private boolean gameStarting = false;

    public JoinLobbyController(doodleMain game, JoinLobbyView view, String lobbyCode) {
        this.game = game;
        this.view = view;
        this.firebase = game.getFirebaseService();
        this.lobbyCode = lobbyCode;

        setupLobbyListeners();
    }

    private void setupLobbyListeners() {
        firebase.setupLobbyListener(lobbyCode, new FirebaseInterface.LobbyStateCallback() {
            @Override
            public void onPlayerJoined(String playerName) {
                // Optional: Could show join notifications
            }

            @Override
            public void onPlayerLeft(String playerName) {
                Gdx.app.postRunnable(() -> {
                    if (playerName.equals(game.getPlayerName())) {
                        view.showError("You were removed from the lobby");
                        if (!gameStarting) {
                            view.returnToMainMenu();
                        }
                    }
                });
            }

            @Override
            public void onGameStarted(String drawerName) {
                gameStarting = true;
                Gdx.app.postRunnable(() -> {
                    view.showStatus("Game starting...");
                    // TODO: Transition to game screen
                    // game.setScreen(new DrawingView(game, lobbyCode, game.getPlayerName().equals(drawerName)));
                });
            }

            @Override
            public void onLobbyClosed() {
                Gdx.app.postRunnable(() -> {
                    if (!gameStarting) {
                        view.showError("Host closed the lobby");
                        view.returnToMainMenu();
                    }
                });
            }

            @Override
            public void onError(String message) {
                Gdx.app.postRunnable(() -> view.showError(message));
            }
        });
    }

    public void leaveLobby() {
        if (!gameStarting) {
            firebase.leaveLobby(lobbyCode, game.getPlayerName());
            view.returnToMainMenu();
        }
    }
}
