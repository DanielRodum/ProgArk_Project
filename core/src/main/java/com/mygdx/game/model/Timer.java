package com.mygdx.game.model;

public class Timer {
    private float timeLeft;
    private float totalTime;
    private boolean running;

    public Timer(float totalTime) {
        this.totalTime = totalTime;
        this.timeLeft = totalTime;
        this.running = false;
    }

    public void start() {
        this.timeLeft = totalTime;
        this.running = true;
    }

    public void update(float delta) {
        if (running) {
            timeLeft -= delta;
            if (timeLeft < 0) {
                timeLeft = 0;
                running = false;
            }
        }
    }

    public void stop() { this.running = false; }
    public float getTimeLeft() { return timeLeft; }
    public boolean isRunning() { return running; }
}
