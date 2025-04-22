package com.mygdx.game.utils;

import com.badlogic.gdx.utils.Timer;
public class RoundTimer {
    private Timer.Task task;
    private int secondsLeft;
    private TimerListener listener;

    public interface TimerListener{
        void onTick(int secondsLeft);
        void onTimeUp();
    }

    public RoundTimer(int startSeconds, TimerListener listener){
        this.secondsLeft = startSeconds;
        this.listener = listener;
    }

    public void start(){
        stop();
        task = new Timer.Task(){
            @Override
            public void run(){
                secondsLeft--;
                if (secondsLeft > 0){
                    listener.onTick(secondsLeft);
                } else {
                    listener.onTick(0);
                    listener.onTimeUp();
                    stop();
                }
            }
        };
        Timer.schedule(task, 1, 1);
    }

    public void stop(){
        if (task != null){
            task.cancel();
            task = null;
        }
    }

    public int getTimeRemaining(){
        return secondsLeft;
    }

    public boolean isRunning(){
        return task != null;
    }
}
