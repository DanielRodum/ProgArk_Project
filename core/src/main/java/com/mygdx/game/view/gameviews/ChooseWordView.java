package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.controller.gamecontrollers.ChooseWordController;

import java.util.List;

public class ChooseWordView implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final ChooseWordController controller;

    public ChooseWordView(ChooseWordController controller) {
        this.controller = controller;
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        showLoading();
        controller.fetchWords();
    }

    private void showLoading() {
        stage.clear();
        Label loadingLabel = new Label("Loading words...", skin);
        loadingLabel.setPosition(Gdx.graphics.getWidth() / 2f - loadingLabel.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f);
        stage.addActor(loadingLabel);
    }

    public void showError(String message) {
        stage.clear();
        Label error = new Label(message, skin);
        error.setFontScale(2);
        error.setPosition(Gdx.graphics.getWidth() / 2f - error.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f);
        stage.addActor(error);
    }

    public void displayWords(List<String> words) {
        stage.clear();

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(50);
        stage.addActor(table);

        Label title = new Label("Choose a Word", skin);
        table.add(title).colspan(2).padBottom(30);
        table.row();

        for (String word : words) {
            TextButton button = new TextButton(word, skin);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.wordSelected(word);
                }
            });
            table.add(button).width(200).height(50).pad(10);
            table.row();
        }
    }

    @Override public void show() {}
    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
