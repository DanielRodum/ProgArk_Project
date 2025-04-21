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
import com.mygdx.game.controller.CreateLobbyController;
import com.mygdx.game.doodleMain;

import java.util.ArrayList;
import java.util.List;

public class CreateLobbyView implements Screen {
    private final doodleMain game;
    private final Stage stage;
    private Skin skin;
    private Label codeLabel;
    private Table playersTable;
    private CreateLobbyController controller;
    private final List<String> players = new ArrayList<>();

    public CreateLobbyView(doodleMain game, String initialCode) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        buildUI(initialCode);

        // wire the controller (it’ll call createLobby and listen)
        controller = new CreateLobbyController(game, this);
    }

    private void buildUI(String code) {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        codeLabel = new Label("Lobby Code: " + code, skin);
        codeLabel.setFontScale(2f);
        root.add(codeLabel).pad(10).row();

        playersTable = new Table();
        ScrollPane scroll = new ScrollPane(playersTable, skin);
        scroll.setFadeScrollBars(false);
        root.add(scroll).size(400, 200).pad(10).row();

        Table btns = new Table();
        TextButton start = new TextButton("Start Game", skin);
        start.getLabel().setFontScale(1.5f);
        start.setColor(Color.GREEN);
        start.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                controller.handleStartGame();
            }
        });
        TextButton leave = new TextButton("Leave Lobby", skin);
        leave.getLabel().setFontScale(1.5f);
        leave.setColor(Color.RED);
        leave.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                controller.leaveLobby();
            }
        });
        btns.add(start).padRight(20);
        btns.add(leave);
        root.add(btns).pad(10);
    }

    /** Called by controller to update the code label. */
    public void setLobbyCode(String code) {
        codeLabel.setText("Lobby Code: " + code);
    }

    /** Add a player to the list. */
    public void addPlayer(String name) {
        if (!players.contains(name)) players.add(name);
        refreshPlayers();
    }

    /** Remove a player. */
    public void removePlayer(String name) {
        players.remove(name);
        refreshPlayers();
    }

    private void refreshPlayers() {
        playersTable.clear();
        for (int i = 0; i < players.size(); i++) {
            Label lbl = new Label((i+1) + ". " + players.get(i), skin);
            lbl.setFontScale(1.5f);
            playersTable.add(lbl).expandX().fillX().pad(5).row();
        }
    }

    /** For controller to guard “start” logic. */
    public int getPlayerCount() {
        return players.size();
    }

    public void showError(String msg) {
        Dialog d = new Dialog("Error", skin);
        d.text(msg);
        d.button("OK");
        d.show(stage);
    }

    public void showStatus(String msg) {
        codeLabel.setText(msg);
    }

    public void returnToMainMenu() {
        game.setScreen(new MainMenuView(game));
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0.05f,0.05f,0.05f,1f);
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
