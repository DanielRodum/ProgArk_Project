package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.controller.WaitingController;
import com.mygdx.game.doodleMain;
import com.mygdx.game.view.gameviews.DrawingView;
import com.mygdx.game.view.gameviews.LeaderboardView;

public class WaitingView implements Screen {
    private final doodleMain game;
    private final Stage stage;
    private final Skin skin;
    private final Table playersTable;
    private final Label codeLabel;
    private final WaitingController controller;
    private final String lobbyCode;

    public WaitingView(doodleMain game, String lobbyCode, boolean isHost) {
        this.game = game;
        this.lobbyCode = lobbyCode;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.skin  = new Skin(Gdx.files.internal("uiskin.json"));

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        codeLabel = new Label("Lobby Code: " + lobbyCode, skin);
        codeLabel.setFontScale(2f);
        root.add(codeLabel).pad(10).row();

        playersTable = new Table();
        ScrollPane scroll = new ScrollPane(playersTable, skin);
        scroll.setFadeScrollBars(false);
        root.add(scroll).size(400,200).pad(10).row();

        Table btns = new Table();
        if (isHost) {
            TextButton start = new TextButton("Start Game", skin);
            start.getLabel().setFontScale(1.5f);
            start.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) {
                    controller.handleStartGame();
                }
            });
            btns.add(start).padRight(20);
        }
        TextButton leave = new TextButton("Leave Lobby", skin);
        leave.getLabel().setFontScale(1.5f);
        leave.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                controller.leaveLobby();
            }
        });
        btns.add(leave);

        root.add(btns).pad(10);

        controller = new WaitingController(game, this, lobbyCode, isHost);
    }

    public void addPlayer(String name) {
        Label l = new Label(name, skin);
        l.setFontScale(1.5f);
        playersTable.add(l).row();
    }
    public void removePlayer(String name) {
        playersTable.clear();
    }
    public void onGameStarted(String drawer) {
        if (drawer.equals(game.getPlayerName())){
            game.setScreen(new DrawingView(game, lobbyCode));
        } else {
            game.setScreen(new LeaderboardView()); 
        }
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
