package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.doodleMain;
import com.mygdx.game.model.Player;
import com.mygdx.game.utils.LeaveLobbyButton;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardView implements Screen {
    private final Stage stage;
    private final Table table;
    private final Skin skin;
    private final doodleMain game;
    private final String lobbyCode;
    private final String currentDrawer;
    private final Runnable onLeave;

    public LeaderboardView(doodleMain game, String lobbyCode, String currentDrawer, Runnable onLeave) {
        this.game = game;
        this.lobbyCode = lobbyCode;
        this.currentDrawer = currentDrawer;
        this.onLeave = onLeave;

        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        // root layout
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        table = new Table();
        table.top();
        ScrollPane scroll = new ScrollPane(table, skin);
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(false);
        root.add(scroll).expand().fill().pad(20);

        // initially empty
        updateLeaderboard(new ArrayList<>());

        // schedule the transition after 8 seconds
        stage.addAction(Actions.sequence(
            Actions.delay(8f),
            Actions.run(() -> {
                // only proceed if we're still on this screen
                if (game.getScreen() instanceof LeaderboardView) {
                    if (game.getPlayerName().equals(currentDrawer)) {
                        game.setScreen(new ChooseWordView(game, lobbyCode));
                    } else {
                        displayWaitingMessage(currentDrawer);
                    }
                }
            })
        ));
    }

    public void updateLeaderboard(List<Player> players) {
        table.clear();

        Label title = new Label("Leaderboard", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        title.setFontScale(6f);
        title.setAlignment(Align.center);
        table.add(title).colspan(2).padBottom(40).row();

        players.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        for (Player p : players) {
            Label name = new Label(p.getName(), skin);
            Label score = new Label(String.valueOf(p.getScore()), skin);
            name.setFontScale(5f);
            score.setFontScale(5f);
            table.add(name).padRight(20);
            table.add(score).padLeft(20).row();
        }
    }

    public void displayWaitingMessage(String drawer) {
        table.row();
        Label wait = new Label("Waiting for " + drawer + " to pick a word...", skin);
        wait.setFontScale(3.5f);
        wait.setAlignment(Align.center);
        table.add(wait).colspan(2).padTop(30);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 0.5f, 0.5f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
        Table bottom = new Table();
        bottom.setFillParent(true);
        bottom.bottom().padBottom(20);
        stage.addActor(bottom);
        LeaveLobbyButton leave = new LeaveLobbyButton(skin, onLeave);
        bottom.add(leave);
    }
    @Override public void resize(int w, int h)  { stage.getViewport().update(w, h, true); }
    @Override public void show()    {}
    @Override public void hide()    {}
    @Override public void pause()   {}
    @Override public void resume()  {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
