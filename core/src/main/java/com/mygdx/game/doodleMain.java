package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.view.MainMenuView;

import java.util.List;

public class doodleMain extends Game {
    private SpriteBatch batch;
    private Texture image;
    private FirebaseInterface firebaseService;
    private List<String> wordBank;
    private AndroidBridge androidBridge;

    public doodleMain(AndroidBridge androidBridge){
        this.androidBridge = androidBridge;
    }

    public void openTutorial(){
        if (androidBridge != null){
            androidBridge.openTutorial();
        }
    }

    public void setFirebaseService(FirebaseInterface service) {
        this.firebaseService = service;
        fetchWords();
    }

    public FirebaseInterface getFirebaseService() {
        return firebaseService;
    }

    public interface AndroidBridge {
        void openTutorial();
    }


    private void fetchWords() {
        if (firebaseService != null) {
            firebaseService.fetchWords(new FirebaseInterface.FirestoreCallback() {
                @Override
                public void onSuccess(List<String> words) {
                    wordBank = words;
                    Gdx.app.log("Firebase", "Loaded words: " + words);
                }

                @Override
                public void onFailure(Exception e) {
                    Gdx.app.error("Firebase", "Failed to load words", e);
                }
            });
        }
    }

    @Override
    public void create(){
        setScreen(new MainMenuView(this));
    }

    @Override
    public void render() {
        super.render();
    }
}
