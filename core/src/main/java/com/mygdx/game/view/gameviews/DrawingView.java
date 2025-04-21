package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.doodleMain;

public class DrawingView implements Screen {
    private final doodleMain game;
    private ShapeRenderer shapeRenderer;

    public DrawingView(doodleMain game) {
        this.game = game;
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Placeholder: drawing logic would go here
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        // shapeRenderer.line(x1, y1, x2, y2); // will come later
        shapeRenderer.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() { shapeRenderer.dispose(); }
}
