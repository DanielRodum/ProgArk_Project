package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mygdx.game.model.GameStateModel;

public class DrawingView extends Table {
    public DrawingView(Skin skin, String word) {
        setFillParent(true);
        center();

        // Display the word at the top for the drawing player
        Label wordLabel = new Label("Word: " + word, skin);
        add(wordLabel).colspan(3).padBottom(20f).row();

        // Add the drawing canvas (this could later be replaced with an actual drawing area)
        Label placeholderLabel = new Label("Draw here!", skin);
        placeholderLabel.setFontScale(2);
        add(placeholderLabel).expand().fill();

        row();
    }
}
