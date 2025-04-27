package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.controller.WaitingController;
import com.mygdx.game.doodleMain;
import com.mygdx.game.utils.LeaveLobbyButton;
import com.mygdx.game.view.gameviews.DrawingView;
import com.mygdx.game.view.gameviews.LeaderboardView;

import java.util.ArrayList;
import java.util.List;

public class WaitingView implements Screen {
    private final doodleMain game;
    private final Stage stage;
    private final Skin skin;
    private final Table playersTable;
    private final Label codeLabel;
    private final WaitingController controller;
    private final String lobbyCode;

    // Keep track of joined players
    private final List<String> players = new ArrayList<>();

    public WaitingView(doodleMain game, String lobbyCode, boolean isHost) {
        this.game      = game;
        this.lobbyCode = lobbyCode;
        this.stage     = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.skin      = new Skin(Gdx.files.internal("uiskin.json"));

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        codeLabel = new Label("Lobby Code: " + lobbyCode, skin);
        codeLabel.setFontScale(5f);
        root.add(codeLabel).pad(50).row();

        playersTable = new Table();
        root.add(playersTable).pad(10).row();

        controller = new WaitingController(game, this, lobbyCode, isHost);

        Table btns = new Table();
        if (isHost) {
            TextButton start = new TextButton("Start Game", skin);
            start.getLabel().setFontScale(3f);
            start.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) {
                    controller.handleStartGame();
                }
            });
            btns.add(start).padRight(20);
        }
        LeaveLobbyButton leave = new LeaveLobbyButton(skin, controller::leaveLobby);
        btns.add(leave);

        root.add(btns).pad(10);
    }

    /** Add a new player and rebuild the table */
    public void addPlayer(String name) {
        if (!players.contains(name)) {
            players.add(name);
            rebuildPlayersTable();
        }
    }

    /** Remove a player and rebuild the table */
    public void removePlayer(String name) {
        if (players.remove(name)) {
            rebuildPlayersTable();
        }
    }

    /** Helper to clear & repopulate the playersTable from our list */
    private void rebuildPlayersTable() {
        playersTable.clear();
        for (String p : players) {
            Label lbl = new Label(p, skin);
            lbl.setFontScale(5f);
            playersTable.add(lbl).row();
        }
    }

    /** Returns how many players are in the lobby */
    public int getPlayerCount() {
        return players.size();
    }

    /** Returns a copy of the current player list */
    public List<String> getPlayers() {
        return new ArrayList<>(players);
    }

    /** Show a one-line status in place of the lobby code */
    public void showStatus(String message) {
        codeLabel.setText(message);
        codeLabel.setColor(Color.YELLOW);
    }

    /** Pop up an error dialog */
    public void showError(String message) {
        Dialog d = new Dialog("Error", skin);
        d.text(message);
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
    @Override public void show()   {}
    @Override public void hide()   {}
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
