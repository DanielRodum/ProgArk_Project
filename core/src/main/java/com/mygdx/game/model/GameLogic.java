package com.mygdx.game.model;

import com.mygdx.game.FirebaseInterface;
import java.util.ArrayList;
import java.util.List;


public class GameLogic {
    private final List<Player> players = new ArrayList<>();
    private String currentWord;

    public GameLogic(FirebaseInterface firebase, String lobbyCode) {
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
                // handled elsewhere
            }

            @Override
            public void onWordChosen(String word) {
                currentWord = word;
            }

            @Override
            public void onLobbyClosed() {
                players.clear();
                currentWord = null;
            }

            @Override
            public void onError(String message) {
                // optionally log or surface error
            }
        });
    }

    /** For å hente ut spillere */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /** For å sette currentWord basert på firebase-logikk */
    public void setCurrentWord(String word) {
        this.currentWord = word;
    }

    /** For å hente currentWord */
    public String getCurrentWord() {
        return currentWord;
    }

    /** Returnerer en skult versjon av ordet for gjetterne (ex: "_ _ _ _") */
    public String getMaskedWord() {
        if (currentWord == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : currentWord.toCharArray()) {
            if (Character.isLetter(c)) sb.append("_ ");
            else                        sb.append(c).append(' ');
        }
        return sb.toString().trim();
    }

    /** Sjekker om ordet gjettet er riktig ord */
    public boolean isCorrectGuess(String guess) {
        if (currentWord == null || guess == null) return false;
        return currentWord.equalsIgnoreCase(guess.trim());
    }
}
