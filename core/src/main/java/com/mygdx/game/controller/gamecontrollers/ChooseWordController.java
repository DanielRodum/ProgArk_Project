package com.mygdx.game.controller.gamecontrollers;

import com.mygdx.game.doodleMain;
import com.mygdx.game.model.WordBank;
import com.mygdx.game.view.gameviews.ChooseWordView;

import java.util.List;

public class ChooseWordController {
    private final doodleMain game;
    private final ChooseWordView view;
    private final WordBank wordBank;

    public ChooseWordController(doodleMain game, ChooseWordView view) {
        this.game = game;
        this.view = view;
        this.wordBank = game.getWordBank();

        loadWordOptions();
    }

    /**
     * Get word options from the WordBank and send them to the view.
     */
    private void loadWordOptions() {
        List<String> wordOptions = wordBank.getRandomOptions(3);
        view.displayWords(wordOptions);
    }

    /**
     * Called by the view when a word is selected.
     */
    public void onWordSelected(String selectedWord) {
        System.out.println("Player selected word: " + selectedWord);

        // Tell the GameLogicController to start the round with this word
        game.getGameLogicController().startRound(selectedWord);
    }
}
