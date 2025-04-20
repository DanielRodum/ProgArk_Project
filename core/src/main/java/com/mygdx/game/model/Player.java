package com.mygdx.game.model;

public class Player {
    private String id;
    private String name;
    private int score;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.score = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void updateStats(boolean guessedCorrectly) {
        if (guessedCorrectly) {
            score += 10; // You can customize the scoring later
        }
    }
}
