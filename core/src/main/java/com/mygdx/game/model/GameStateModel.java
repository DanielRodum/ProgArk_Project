package com.mygdx.game.model;

import java.util.*;

public class GameStateModel {
    private List<Player> players;
    private String currentWord;
    private Player currentDrawer;
    private int currentRound;
    private Map<String, Integer> scores;
    private int timeLimit;
    private List<Guess> guesses;
    private List<String> wordBank;

    public GameStateModel(List<Player> players, int timeLimit, List<String> wordBank) {
        this.players = players;
        this.timeLimit = timeLimit;
        this.wordBank = wordBank;
        this.currentRound = 0;
        this.scores = new HashMap<>();
        this.guesses = new ArrayList<>();
        for (Player player : players) {
            scores.put(player.getId(), 0);
        }
    }

    public void startNewRound(String selectedWord) {
        this.currentWord = selectedWord;
        this.currentDrawer = players.get(currentRound % players.size());
        this.currentRound++;
        this.guesses.clear();
    }

    public List<String> getWordOptionsForDrawer(int count) {
        Collections.shuffle(wordBank);
        return wordBank.subList(0, Math.min(count, wordBank.size()));
    }

    public void addGuess(Player player, String guess, boolean isCorrect) {
        guesses.add(new Guess(player, guess, isCorrect));
        if (isCorrect) {
            int newScore = scores.get(player.getId()) + calculatePoints();
            scores.put(player.getId(), newScore);
        }
    }

    private int calculatePoints() {
        return Math.max(10 - guesses.size(), 1);
    }

    public String getMaskedWord() {
        return currentWord.replaceAll("[A-Za-z]", "_");
    }

    // Getters
    public List<Player> getPlayers() {
        return players;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public Player getCurrentDrawer() {
        return currentDrawer;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public List<Guess> getGuesses() {
        return guesses;
    }
}
