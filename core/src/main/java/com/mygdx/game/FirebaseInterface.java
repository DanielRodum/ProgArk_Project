package com.mygdx.game;

import java.util.Map;

public interface FirebaseInterface {
    // Lobby Management
    interface LobbyCallback {
        void onSuccess(String lobbyCode);
        void onFailure(String error);
    }

    interface LobbyStateCallback {
        void onPlayerJoined(String playerName);
        void onPlayerLeft(String playerName);
        void onGameStarted(String drawerName);
        void onLobbyClosed();

        void onError(String message);
    }

    void createLobby(String hostName, LobbyCallback callback);
    void joinLobby(String lobbyCode, String playerName, LobbyCallback callback);
    void startGame(String lobbyCode, String drawerName);
    void initializeDatabaseStructure(Runnable onComplete);
    void leaveLobby(String lobbyCode, String playerName);
    void setupLobbyListener(String lobbyCode, LobbyStateCallback callback);
}
