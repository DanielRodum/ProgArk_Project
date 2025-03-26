package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.List;

public class doodleMain extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private FirebaseInterface firebaseService;
    private List<String> wordBank;

    public void setFirebaseService(FirebaseInterface service) {
        this.firebaseService = service;
        fetchWords();
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
    public void create() {
        batch = new SpriteBatch();
        image = new Texture(Gdx.files.internal("libgdx.png")); // Your texture here
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
