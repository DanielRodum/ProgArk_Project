package com.mygdx.game.model;

import com.mygdx.game.FirebaseInterface;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the current lobby state: player list, current drawer, and current word.
 */
public class GameLogic {
    private final List<Player> players = new ArrayList<>();
    private String currentWord;
    private String currentDrawer;

    public GameLogic(FirebaseInterface firebase, String lobbyCode) {
        // Listen to lobby state changes
        firebase.setupLobbyListener(lobbyCode, new FirebaseInterface.LobbyStateCallback() {
            @Override
            public void onPlayerJoined(String playerName) {
                players.add(new Player(playerName, playerName));
            }

            @Override
            public void onPlayerLeft(String playerName) {
                players.removeIf(p -> p.getName().equals(playerName));
            }

            @Override
            public void onGameStarted(String drawerName) {
                // track current drawer
                currentDrawer = drawerName;
                // update player flags
                for (Player p : players) {
                    p.setDrawer(p.getName().equals(drawerName));
                }
            }

            @Override
            public void onWordChosen(String word) {
                currentWord = word;
            }

            @Override
            public void onLobbyClosed() {
                players.clear();
                currentWord = null;
                currentDrawer = null;
            }

            @Override
            public void onError(String message) {
                // optionally log or surface error
            }
        });
    }

    /** Returns a copy of players and their current scores + drawer status */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /** Returns the current drawer name */
    public String getCurrentDrawer() {
        return currentDrawer;
    }

    /** Returns a masked version of the current word (e.g. "_ _ _") */
    public String getMaskedWord() {
        if (currentWord == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : currentWord.toCharArray()) {
            if (Character.isLetter(c)) sb.append("_ ");
            else                        sb.append(c).append(' ');
        }
        return sb.toString().trim();
    }

    /** Checks if a guess matches the current word (case-insensitive) */
    public boolean isCorrectGuess(String guess) {
        if (currentWord == null || guess == null) return false;
        return currentWord.equalsIgnoreCase(guess.trim());
    }
}
