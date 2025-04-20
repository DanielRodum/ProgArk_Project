package com.mygdx.game.model;

public class Guess {
    private Player player;
    private String guess;
    private boolean correct;

    public Guess(Player player, String guess, boolean correct) {
        this.player = player;
        this.guess = guess;
        this.correct = correct;
    }

    public Player getPlayer() {
        return player;
    }

    public String getGuess() {
        return guess;
    }

    public boolean isCorrect() {
        return correct;
    }
}
