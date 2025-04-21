package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.view.gameviews.ChooseWordView;

import java.util.List;

public class ChooseWordController {
    private final doodleMain game;
    private final ChooseWordView view;
    private final FirebaseInterface firebase;
    private final String lobbyCode;

    public ChooseWordController(doodleMain game, ChooseWordView view,
                                String lobbyCode, boolean isHost) {
        this.game      = game;
        this.view      = view;
        this.firebase  = game.getFirebaseService();
        this.lobbyCode = lobbyCode;
        fetchWords();
    }

    private void fetchWords() {
        firebase.fetchWords(new FirebaseInterface.FirestoreCallback() {
            @Override public void onSuccess(List<String> words) {
                Gdx.app.postRunnable(() -> view.displayWords(words));
            }
            @Override public void onFailure(Exception e) {
                Gdx.app.postRunnable(() -> view.showError("Failed to load words"));
            }
        });
    }

    public void selectWord(String word) {
        firebase.startDrawingRound(lobbyCode, word, new FirebaseInterface.LobbyCallback() {
            @Override public void onSuccess(String msg) {
                // stub: next screen is implemented by someone else
            }
            @Override public void onFailure(String err) {
                Gdx.app.postRunnable(() -> view.showError("Could not start round"));
            }
        });
    }
}
