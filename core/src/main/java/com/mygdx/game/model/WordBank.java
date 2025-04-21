//Skal hentes fra firebase
//

package com.mygdx.game.model;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.FirebaseInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordBank {
    private FirebaseInterface firebaseService;
    private List<String> allWords = new ArrayList<>();

    public void setFirebaseService(FirebaseInterface service) {
        this.firebaseService = service;
        fetchWords();
    }

    public void fetchWords() {
        if (firebaseService != null) {
            firebaseService.fetchWords(new FirebaseInterface.FirestoreCallback() {
                @Override
                public void onSuccess(List<String> words) {
                    allWords = words;
                    Gdx.app.log("WordBank", "Words loaded: " + words.size());
                }

                @Override
                public void onFailure(Exception e) {
                    Gdx.app.error("WordBank", "Failed to load words", e);
                }
            });
        }
    }

    public List<String> getRandomOptions(int count) {
        if (allWords.size() < count) return allWords;
        List<String> shuffled = new ArrayList<>(allWords);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, count);
    }
}
