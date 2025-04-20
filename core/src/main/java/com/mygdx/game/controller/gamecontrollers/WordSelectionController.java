package com.mygdx.game.controller.gamecontrollers;

import com.mygdx.game.model.GameStateModel;
import com.mygdx.game.view.gameViews.WordSelectionView;
import com.mygdx.game.doodleMain;

import java.util.List;

public class WordSelectionController {

    private doodleMain game;
    private GameStateModel gameState;
    private WordSelectionView view;

    public WordSelectionController(doodleMain game, GameStateModel gameState, WordSelectionView view) {
        this.game = game;
        this.gameState = gameState;
        this.view = view;
    }

    public void presentWordOptions() {
        List<String> wordOptions = gameState.getWordOptionsForDrawer(3);
        view.showWordOptions(wordOptions);
    }

    public void handleWordSelected(String selectedWord) {
        gameState.startNewRound(selectedWord);
        System.out.println("Drawer selected word: " + selectedWord);

        // After selection, route drawer and guessers to appropriate views
        game.routeToDrawingAndGuessingViews();
    }
}
