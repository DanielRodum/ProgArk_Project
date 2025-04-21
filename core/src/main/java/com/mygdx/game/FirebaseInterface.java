package com.mygdx.game;

import java.util.List;

public interface FirebaseInterface {
    //
    // Word‐round APIs
    //
    /** Fetches the list of words to choose from. */
    void fetchWords(FirestoreCallback callback);
    /** Starts the drawing round with the chosen word. */
    void startDrawingRound(String lobbyCode, String word, LobbyCallback callback);

    //
    // Lobby management callbacks
    //
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

    //
    // Firestore (word list) callback
    //
    interface FirestoreCallback {
        void onSuccess(List<String> words);
        void onFailure(Exception e);
    }

    //
    // Lobby management methods
    //
    void createLobby(String hostName, LobbyCallback callback);
    void joinLobby(String lobbyCode, String playerName, LobbyCallback callback);
    void startGame(String lobbyCode, String drawerName);
    void leaveLobby(String lobbyCode, String playerName);
    void setupLobbyListener(String lobbyCode, LobbyStateCallback callback);
    void initializeDatabaseStructure(Runnable onComplete);
}
