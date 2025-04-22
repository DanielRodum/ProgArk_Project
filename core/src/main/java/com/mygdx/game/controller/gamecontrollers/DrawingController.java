package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.view.LeaderboardView;
import com.mygdx.game.view.gameviews.DrawingView;

import java.util.Timer;
import java.util.TimerTask;

public class DrawingController {
    private final doodleMain game;
    private final DrawingView view;
    private final FirebaseInterface firebase;
    private final String lobbyCode;
    private String currentWord;
    private int secondsLeft = 30;
    private Timer timer;

    public DrawingController(doodleMain game, DrawingView view, String lobbyCode){
        this.game = game;
        this.view = view;
        this.lobbyCode = lobbyCode;
        this.firebase = game.getFirebaseService();
        fetchWord();
        startTimer();
    }

    private void fetchWord(){
        firebase.getChosenWord(lobbyCode, new FirebaseInterface.WordCallback(){
            @Override
            public void onSuccess(String word){
                currentWord = word;
                view.setWord(word);
            }

            @Override
            public void onFailure(Exception e){
                view.setWord("<Error>");
            }
        });
    }
    private void startTimer(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Gdx.app.postRunnable(()->{
                    secondsLeft--;
                    view.setTime(secondsLeft);
                    if (secondsLeft <= 0){
                        timer.cancel();
                        Gdx.app.postRunnable(()->{
                            game.setScreen(new LeaderboardView());
                        });
                    }
                });
            }
        }, 1000, 1000);
    }

    public void stopTimer(){
        if (timer != null) timer.cancel();
    }
}
