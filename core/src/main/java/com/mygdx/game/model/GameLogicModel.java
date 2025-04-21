// Holde styr på currentWord
// Holde styr på hvor mange som er ferdig med å gjette
//


package com.mygdx.game.model;

import java.util.*;

public class GameLogicModel {
    private List<Player> players = new ArrayList<>();
    private int currentDrawerIndex = 0;
    private String currentWord = "";
    private boolean roundActive = false;
    private List<Guess> guesses = new ArrayList<>();

    public void addPlayer(Player player) {
        players.add(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentDrawer() {
        return players.get(currentDrawerIndex);
    }

    public void startRound(String word) {
        for (Player player : players) {
            player.setDrawer(false);
        }

        currentWord = word;
        getCurrentDrawer().setDrawer(true);
        roundActive = true;
        guesses.clear();
    }

    public void endRound() {
        roundActive = false;
    }

    public boolean isRoundActive() {
        return roundActive;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void addGuess(Guess guess) {
        guesses.add(guess);
        if (guess.getWord().equalsIgnoreCase(currentWord)) {
            guess.markCorrect();
            for (Player p : players) {
                if (p.getId().equals(guess.getPlayerId())) {
                    p.addScore(10);
                }
            }
        }
    }

    public List<Guess> getGuesses() {
        return guesses;
    }

    public void nextDrawer() {
        currentDrawerIndex = (currentDrawerIndex + 1) % players.size();
    }
}
