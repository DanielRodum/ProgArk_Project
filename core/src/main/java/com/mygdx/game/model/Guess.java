package com.mygdx.game.model;

public class Guess {
    private String playerId;
    private String word;
    private boolean correct;

    public Guess(String playerId, String word) {
        this.playerId = playerId;
        this.word = word;
        this.correct = false;
    }

    public String getPlayerId() { return playerId; }
    public String getWord() { return word; }
    public boolean isCorrect() { return correct; }

    public void markCorrect() { this.correct = true; }
}
