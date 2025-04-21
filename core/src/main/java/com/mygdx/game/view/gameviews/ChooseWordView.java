package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.doodleMain;
import com.mygdx.game.controller.gamecontrollers.ChooseWordController;

import java.util.List;

public class ChooseWordView implements Screen {
    private final doodleMain game;
    private final Stage stage;
    private final Skin skin;
    private final ChooseWordController controller;
    private Table table;

    public ChooseWordView(doodleMain game, ChooseWordController controller) {
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
        table.top().padTop(50);
        stage.addActor(table);

        Label title = new Label("Choose a Word", skin);
        title.setFontScale(2f);
        title.setAlignment(Align.center);
        table.add(title).colspan(1).padBottom(30);
        table.row();
    }

    /**
     * Called by the controller to display word options to the player.
     */
    public void displayWords(List<String> words) {
        for (String word : words) {
            TextButton wordButton = new TextButton(word, skin);
            wordButton.getLabel().setFontScale(1.5f);

            wordButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.onWordSelected(word);
                }
            });

            table.add(wordButton).width(300).height(80).padBottom(20);
            table.row();
        }
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
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
