package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.controller.gamecontrollers.ChooseWordController;
import com.mygdx.game.doodleMain;

import java.util.List;

public class ChooseWordView implements Screen {
    private final doodleMain game;
    private final String lobbyCode;
    private final Stage stage;
    private final Skin skin;
    private ChooseWordController controller;

    public ChooseWordView(doodleMain game, String lobbyCode) {
        this.game      = game;
        this.lobbyCode = lobbyCode;

        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.skin  = new Skin(Gdx.files.internal("uiskin.json"));

        showLoading();
        controller = new ChooseWordController(game, this, lobbyCode);
    }

    private void showLoading() {
        stage.clear();
        Label lbl = new Label("Loading words...", skin);
        lbl.setFontScale(7f);
        Table t = new Table();
        t.setFillParent(true);
        t.add(lbl);
        stage.addActor(t);
    }

    public void displayWords(List<String> words) {
        stage.clear();
        Table t = new Table();
        t.setFillParent(true);
        t.top().padTop(100);
        stage.addActor(t);

        Label title = new Label("Choose a Word:", skin);
        title.setFontScale(7f);
        t.add(title).colspan(2).padBottom(50).row();

        for (String w : words) {
            TextButton b = new TextButton(w, skin);
            b.getLabel().setFontScale(3f);
            b.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) {
                    controller.selectWord(w);
                }
            });
            t.add(b).width(400).height(150).pad(10).row();
        }
    }

    public void showError(String msg) {
        Dialog d = new Dialog("Error", skin);
        d.text(msg);
        d.button("OK");
        d.show(stage);
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(1f,0.5f,0.5f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int w,int h) { stage.getViewport().update(w,h,true); }
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
