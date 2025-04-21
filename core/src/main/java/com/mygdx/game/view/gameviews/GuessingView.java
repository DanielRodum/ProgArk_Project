package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.doodleMain;
import com.mygdx.game.controller.gamecontrollers.GuessingController;

public class GuessingView implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final GuessingController controller;

    public GuessingView(doodleMain game) {
        this.stage = new Stage();
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.controller = new GuessingController(game);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label guessLabel = new Label("Enter your guess:", skin);
        guessLabel.setFontScale(1.2f);

        TextField guessField = new TextField("", skin);
        TextButton submitBtn = new TextButton("Submit", skin);

        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.submitGuess(guessField.getText());
                guessField.setText("");
            }
        });

        table.add(guessLabel).padBottom(20).row();
        table.add(guessField).width(300).padBottom(10).row();
        table.add(submitBtn).width(200).height(60);
    }

    @Override public void render(float delta) { stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
