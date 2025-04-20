package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

/**
 * View displayed to guessing players while the drawer selects a word.
 */
public class WaitingForWordView extends Table {
    /**
     * Constructs the waiting view.
     *
     * @param skin        UI skin to style the label
     * @param drawerName  Name of the player who is choosing a word
     */
    public WaitingForWordView(Skin skin, String drawerName) {
        super();
        setFillParent(true);
        // Center the content
        defaults().expand().fill().center();

        Label waitingLabel = new Label(drawerName + " is choosing a word...", skin);
        waitingLabel.setAlignment(Align.center);

        add(waitingLabel);
    }
}
