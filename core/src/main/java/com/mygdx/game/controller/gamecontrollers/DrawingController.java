package com.mygdx.game.controller.gamecontrollers;

import com.mygdx.game.model.GameStateModel;
import com.mygdx.game.view.gameViews.DrawingView;
import com.mygdx.game.model.Player;

public class DrawingController {

    private GameStateModel gameState;
    private DrawingView view;

    public DrawingController(GameStateModel gameState, DrawingView view) {
        this.gameState = gameState;
        this.view = view;
    }

    public void startDrawingPhase() {
        String currentWord = gameState.getCurrentWord();
        view.displayWord(currentWord);
    }

    public void handleStroke(float x, float y) {
        // You can extend this for drawing logic later
        view.drawAt(x, y);
    }

    public Player getCurrentDrawer() {
        return gameState.getCurrentDrawer();
    }
}
