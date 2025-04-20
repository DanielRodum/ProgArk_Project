package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.game.model.GameStateModel;

import java.util.List;

public class WordSelectionView extends Table {
    public interface WordSelectionListener {
        void onWordSelected(String word);
    }

    public WordSelectionView(Skin skin, List<String> wordOptions, WordSelectionListener listener) {
        super();
        setFillParent(true);
        center();

        Label titleLabel = new Label("Choose a word", skin);
        add(titleLabel).colspan(3).padBottom(20f).row();

        for (String word : wordOptions) {
            TextButton button = new TextButton(word, skin);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    listener.onWordSelected(word); // Trigger word selection
                }
            });
            add(button).pad(10f);
            row();
        }
    }
}
