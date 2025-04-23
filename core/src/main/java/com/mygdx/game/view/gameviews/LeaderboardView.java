package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.model.Player;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardView implements Screen {
    private Stage stage;
    private Table table;
    private Skin skin;

    public LeaderboardView() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        create();
    }

    public void create() {
        stage.clear();

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        table = new Table();
        table.top();

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        rootTable.add(scrollPane).expand().fill().pad(20);

        Gdx.input.setInputProcessor(stage);

        updateLeaderboard(new ArrayList<>());
    }

    public void updateLeaderboard(List<Player> players) {
        table.clear();

        Label title = new Label("Leaderboard", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        title.setFontScale(6.0f);
        title.setAlignment(Align.center);
        table.add(title).colspan(2).padBottom(40);
        table.row();

        players.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        for (Player player : players) {
            Label playerLabel = new Label(player.getName(), skin);
            Label scoreLabel = new Label(String.valueOf(player.getScore()), skin);

            playerLabel.setFontScale(5.0f);
            scoreLabel.setFontScale(5.0f);

            table.add(playerLabel).padRight(20);
            table.add(scoreLabel).padLeft(20);
            table.row();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
