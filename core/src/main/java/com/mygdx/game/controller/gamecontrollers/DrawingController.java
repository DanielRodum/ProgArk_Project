package com.mygdx.game.controller.gamecontrollers;

import com.mygdx.game.doodleMain;
import com.mygdx.game.view.gameviews.DrawingView;

public class DrawingController {
    private final doodleMain game;
    private final DrawingView view;

    public DrawingController(doodleMain game, DrawingView view) {
        this.game = game;
        this.view = view;
        // Immediately show the word on the canvas
        view.displayWord(game.getGameLogicController().getCurrentWord());
    }

    /**
     * Called by the DrawingView whenever the user drags/touches.
     */
    public void onStroke(float x, float y) {
        view.drawAt(x, y);
        // TODO: sync stroke to other players (via FirebaseController)
    }

    /**
     * Call when the drawer finishes (timer runs out or user ends).
     */
    public void onDrawingFinished() {
        // TODO: notify GameLogicController that drawing is done
        // game.getGameLogicController().endDrawingPhase();
    }
}
