package com.mygdx.game.controller.gamecontrollers;

import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.view.gameviews.ChooseWordView;

import java.util.Collections;
import java.util.List;

public class ChooseWordController {
    private final doodleMain game;
    private final ChooseWordView view;

    public ChooseWordController(doodleMain game, ChooseWordView view) {
        this.game = game;
        this.view = view;
    }

    public void fetchWords() {
        FirebaseInterface firebase = game.getFirebaseService();
        if (firebase != null) {
            firebase.fetchWords(new FirebaseInterface.FirestoreCallback() {
                @Override
                public void onSuccess(List<String> words) {
                    Collections.shuffle(words);
                    List<String> options = words.subList(0, Math.min(3, words.size()));
                    view.displayWords(options);
                }

                @Override
                public void onFailure(Exception e) {
                    view.showError("Failed to load words");
                }
            });
        }
    }

    public void wordSelected(String word) {
        System.out.println("Word chosen: " + word);
        // Pass this word into game logic
        game.getGameLogicController().startRoundWithWord(word); // You’d need this method
        // Transition to DrawingView or similar
    }
}
