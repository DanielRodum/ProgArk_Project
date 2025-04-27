package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import java.util.List;
import java.util.Map;

/** Defines all Firebase operations for lobby + word-round. */
public interface FirebaseInterface {
    // existing callbacks…
    void fetchWords(FirestoreCallback callback);
    void startDrawingRound(String lobbyCode, String word, String drawer, LobbyCallback callback);
    void sendStroke(String lobbyCode, String strokeId, List<Vector2> points, String colorHex);
    void subscribeToStrokes(String lobbyCode, StrokeCallback cb);
    void recordGuess(String lobbyCode, String playerName);
    void subscribeToGuesses(String lobbyCode, GuessesCallback callback);

    // ★ new “live scores” API ★
    void subscribeToScores(String lobbyCode, ScoresCallback callback);

    interface ScoresCallback {
        void onScoresUpdated(Map<String,Integer> scores);
    }

    interface GuessesCallback { void onAllGuessed(); }

    interface StrokeCallback {
        void onStrokeAdded(String strokeId, List<Vector2> points, String colorHex);
    }

    interface FirestoreCallback {
        void onSuccess(List<String> words);
        void onFailure(Exception e);
    }

    interface WordCallback {
        void onSuccess(String word);
        void onFailure(Exception e);
    }

    interface LobbyCallback {
        void onSuccess(String lobbyCode);
        void onFailure(String error);
    }

    interface LobbyStateCallback {
        void onPlayerJoined(String playerName);
        void onPlayerLeft(String playerName);
        void onGameStarted(String drawerName);
        void onWordChosen(String word);
        void onLobbyClosed();
        void onError(String message);
    }

    interface PlayersCallback {
        void onSuccess(List<String> players);
        void onFailure(String error);
    }

    interface TimerCallback {
        void onTimerUpdated(long startTimeMs, long durationS);
    }


    void saveChosenWord(String lobbyCode, String word, LobbyCallback callback);
    void getChosenWord(String lobbyCode, WordCallback callback);
    void createLobby(String hostName, LobbyCallback callback);
    void joinLobby(String lobbyCode, String playerName, LobbyCallback callback);
    void fetchPlayers(String lobbyCode, PlayersCallback callback);
    void startGame(String lobbyCode, String drawerName);
    void leaveLobby(String lobbyCode, String playerName);
    void setupLobbyListener(String lobbyCode, LobbyStateCallback callback);
    void initializeDatabaseStructure(Runnable onComplete);
    void updatePlayerScore(String lobbyCode, String playerName, int score);
    void subscribeToTimer(String lobbyCode, TimerCallback callback);
}
