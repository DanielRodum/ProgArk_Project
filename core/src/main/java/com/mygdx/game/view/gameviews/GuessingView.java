package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.doodleMain;
import com.mygdx.game.controller.gamecontrollers.GuessingController;

public class GuessingView implements Screen {
    private final doodleMain game;
    private final GuessingController controller;
    private final Stage stage;
    private final Skin skin;
    private Table table;
    private Label maskedWordLabel;
    private TextField guessField;
    private TextButton submitBtn;
    private Label feedbackLabel;

    public GuessingView(doodleMain game, GuessingController controller) {
        this.game = game;
        this.controller = controller;
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        createUI();
    }

    private void createUI() {
        table = new Table();
        table.setFillParent(true);
        table.top().padTop(Gdx.graphics.getHeight() * 0.1f);
        stage.addActor(table);

        maskedWordLabel = new Label("", skin);
        maskedWordLabel.setFontScale(1.5f);
        maskedWordLabel.setAlignment(Align.center);

        guessField = new TextField("", skin);
        guessField.setMessageText("Your guess…");

        submitBtn = new TextButton("Submit", skin);
        submitBtn.getLabel().setFontScale(1.2f);
        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onGuessSubmitted(guessField.getText());
                guessField.setText("");
            }
        });

        feedbackLabel = new Label("", skin);
        feedbackLabel.setFontScale(1.2f);
        feedbackLabel.setAlignment(Align.center);

        table.add(maskedWordLabel).width(Gdx.graphics.getWidth() * 0.8f).padBottom(20f).row();
        table.add(guessField).width(Gdx.graphics.getWidth() * 0.6f).padBottom(20f).row();
        table.add(submitBtn).width(Gdx.graphics.getWidth() * 0.4f).height(Gdx.graphics.getHeight() * 0.1f).padBottom(20f).row();
        table.add(feedbackLabel).width(Gdx.graphics.getWidth() * 0.8f);
    }

    public void displayMaskedWord(String masked) {
        maskedWordLabel.setText(masked);
    }

    public void showCorrectFeedback() {
        feedbackLabel.setText("Correct!");
        feedbackLabel.setColor(Color.GREEN);
    }

    public void showIncorrectFeedback() {
        feedbackLabel.setText("Try again…");
        feedbackLabel.setColor(Color.RED);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
