package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygdx.game.doodleMain;

public class WaitingForWordView implements Screen {
    private final Stage stage;
    private final Skin skin;

    public WaitingForWordView(doodleMain game, String playerName) {
        this.stage = new Stage();
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label waitingLabel = new Label(playerName + " is choosing word. Please wait", skin);
        waitingLabel.setFontScale(4f);
        table.add(waitingLabel);
    }

    @Override public void render(float delta) { stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
