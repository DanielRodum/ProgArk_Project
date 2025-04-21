package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.controller.ChooseWordController;
import com.mygdx.game.doodleMain;

import java.util.List;

public class ChooseWordView implements Screen {
    private final doodleMain game;
    private final String lobbyCode;
    private final boolean isHost;
    private final Stage stage;
    private final Skin skin;
    private ChooseWordController controller;

    public ChooseWordView(doodleMain game, String lobbyCode, boolean isHost) {
        this.game      = game;
        this.lobbyCode = lobbyCode;
        this.isHost    = isHost;

        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.skin  = new Skin(Gdx.files.internal("uiskin.json"));

        showLoading();
        controller = new ChooseWordController(game, this, lobbyCode, isHost);
    }

    private void showLoading() {
        stage.clear();
        Label lbl = new Label("Loading words...", skin);
        lbl.setFontScale(2f);
        Table t = new Table();
        t.setFillParent(true);
        t.add(lbl);
        stage.addActor(t);
    }

    public void displayWords(List<String> words) {
        stage.clear();
        Table t = new Table();
        t.setFillParent(true);
        t.top().padTop(50);
        stage.addActor(t);

        Label title = new Label("Choose a Word", skin);
        title.setFontScale(3f);
        t.add(title).colspan(2).padBottom(30).row();

        for (String w : words) {
            TextButton b = new TextButton(w, skin);
            b.getLabel().setFontScale(2f);
            b.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) {
                    controller.selectWord(w);
                }
            });
            t.add(b).width(300).height(60).pad(10).row();
        }
    }

    public void showError(String msg) {
        Dialog d = new Dialog("Error", skin);
        d.text(msg);
        d.button("OK");
        d.show(stage);
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0.1f,0.1f,0.1f,1f);
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
