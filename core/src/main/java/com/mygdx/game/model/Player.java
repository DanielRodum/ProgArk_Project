

package com.mygdx.game.model;

public class Player {
    private String id;
    private String name;
    private int score;
    private boolean isDrawer;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.score = 0;
        this.isDrawer = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getScore() { return score; }
    public boolean isDrawer() { return isDrawer; }

    public void setScore(int score) { this.score = score; }
    public void addScore(int points) { this.score += points; }
    public void setDrawer(boolean isDrawer) { this.isDrawer = isDrawer; }
}
