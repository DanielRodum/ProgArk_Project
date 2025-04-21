package com.mygdx.game.controller.gamecontrollers;

import com.mygdx.game.doodleMain;
import com.mygdx.game.view.gameviews.WaitingForWordView;

public class WaitingForWordController {
    private final doodleMain game;
    private final WaitingForWordView view;

    public WaitingForWordController(doodleMain game, WaitingForWordView view, String drawerName) {
        this.game = game;
        this.view = view;
        showWaitingScreen(drawerName);
    }

    private void showWaitingScreen(String drawerName) {
        view.showWaitingMessage(drawerName + " is choosing a word…");
    }

    /**
     * Wherever you detect the drawer has chosen a word,
     * you’ll call into your GameLogicController to transition to the drawing/guessing phase.
     */
    public void onWordChosenRemotely(String chosenWord) {
        // TODO: game.getGameLogicController().startRound(chosenWord);
    }
}
