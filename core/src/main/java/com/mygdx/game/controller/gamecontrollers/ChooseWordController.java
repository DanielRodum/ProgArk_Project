package com.mygdx.game.controller.gamecontrollers;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.view.gameviews.ChooseWordView;
import com.mygdx.game.view.gameviews.DrawingView;

import java.util.List;

public class ChooseWordController {
    private final doodleMain game;
    private final ChooseWordView view;
    private final FirebaseInterface firebase;
    private final String lobbyCode;

    public ChooseWordController(doodleMain game,
                                ChooseWordView view,
                                String lobbyCode) {
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

    /** Called when the drawer taps one of the three options.
     Pushes the choice into Firebase under "currentWord" */
    public void selectWord(String word) {
        firebase.saveChosenWord(lobbyCode, word, new FirebaseInterface.LobbyCallback(){
            @Override
            public void onSuccess(String msg) {
                firebase.startDrawingRound(lobbyCode, word, game.getPlayerName(), new FirebaseInterface.LobbyCallback(){
                    @Override
                    public void onSuccess(String code){
                        Gdx.app.postRunnable(()->{
                            game.setScreen(new DrawingView(game, lobbyCode));
                        });
                    }
                    @Override
                    public void onFailure(String error){
                        Gdx.app.postRunnable(()->view.showError("Failed to start round: "+error));
                    }
                });
            }
            @Override
            public void onFailure(String err) {
                Gdx.app.postRunnable(()->view.showError("Could not save word: "+err));
            }
        });
    }
}
