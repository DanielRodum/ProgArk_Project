package com.mygdx.game.view;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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

        // Add test player scores
        List<PlayerScore> testScores = new ArrayList<>();
        testScores.add(new PlayerScore("Alice", 1500));
        testScores.add(new PlayerScore("Bob", 1200));
        testScores.add(new PlayerScore("Charlie", 1800));
        testScores.add(new PlayerScore("David", 1100));
        testScores.add(new PlayerScore("Eve", 1400));

        // Initialize leaderboard with test data
        updateLeaderboard(testScores);
    }

    public void updateLeaderboard(List<PlayerScore> scores) {
        table.clear();

        Label title = new Label("Leaderboard", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        title.setFontScale(6.0f);
        title.setAlignment(Align.center);
        table.add(title).colspan(2).padBottom(40);
        table.row();

        // Convert immutable list to a mutable list before sorting
        List<PlayerScore> mutableScores = new ArrayList<>(scores);
        mutableScores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        for (PlayerScore player : mutableScores) {
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

    // Other unused Screen methods
    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    // Mock PlayerScore class
    // Replace logic with Player class when that is implemented
    public static class PlayerScore {
        private final String name;
        private final int score;

        public PlayerScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }
}
