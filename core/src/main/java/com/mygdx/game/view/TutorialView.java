package com.mygdx.game.view;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.doodleMain;

public class TutorialView implements Screen {
    private final doodleMain game;
    private final Stage stage;

    public TutorialView(doodleMain game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        // Kick off the platform tutorial video/activity
        this.game.openTutorial();
    }

    @Override public void show() {}
    @Override public void render(float delta) {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
