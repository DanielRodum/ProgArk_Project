package com.mygdx.game;

import java.util.List;

/** Defines all Firebase operations for lobby + word‐round. */
public interface FirebaseInterface {
    // Word‐round APIs
    void fetchWords(FirestoreCallback callback);
    void startDrawingRound(String lobbyCode, String word, String drawer, LobbyCallback callback);

    // Lobby‐management callbacks
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

    // Word‐list fetch callback
    interface FirestoreCallback {
        void onSuccess(List<String> words);
        void onFailure(Exception e);
    }

    interface WordCallback {
        void onSuccess(String word);
        void onFailure(Exception e);
    }

    void startDrawingRound(String l, String w, LobbyCallback cb);

    void saveChosenWord(String lobbyCode, String word, LobbyCallback callback);
    void getChosenWord(String lobbyCode, WordCallback callback);

    // Lobby‐management methods
    void createLobby(String hostName, LobbyCallback callback);
    void joinLobby(String lobbyCode, String playerName, LobbyCallback callback);
    void startGame(String lobbyCode, String drawerName);
    void leaveLobby(String lobbyCode, String playerName);
    void setupLobbyListener(String lobbyCode, LobbyStateCallback callback);
    void initializeDatabaseStructure(Runnable onComplete);
}
