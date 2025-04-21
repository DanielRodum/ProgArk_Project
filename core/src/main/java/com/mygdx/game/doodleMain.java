package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.controller.gamecontrollers.GameLogicController;
import com.mygdx.game.model.GameLogicModel;
import com.mygdx.game.model.Player;
import com.mygdx.game.model.WordBank;
import com.mygdx.game.view.gameviews.MainMenuView;

public class doodleMain extends Game {
    private FirebaseInterface firebaseService;
    private WordBank wordBank;
    private GameLogicController gameLogicController;

    @Override
    public void create() {
        // 1) Create or retrieve the local player (from login/lobby flow)
        Player me = new Player("unique‑id‑123", "MyName");

        // 2) Initialize your shared models
        wordBank = new WordBank();
        GameLogicModel gameModel = new GameLogicModel();

        // 3) Create the central game logic controller — passing in the local player
        gameLogicController = new GameLogicController(this, gameModel, wordBank, me);

        // 4) Show the main menu
        setScreen(new MainMenuView(this));
    }

    public void setFirebaseService(FirebaseInterface firebaseService) {
        this.firebaseService = firebaseService;
        if (wordBank != null) wordBank.setFirebaseService(firebaseService);
    }

    public FirebaseInterface getFirebaseService() {
        return firebaseService;
    }

    public WordBank getWordBank() {
        return wordBank;
    }

    /** Expose the controller so views/controllers can use it. */
    public GameLogicController getGameLogicController() {
        return gameLogicController;
    }

    @Override
    public void render() {
        super.render();
    }
}
