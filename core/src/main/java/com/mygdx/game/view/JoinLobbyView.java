package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.controller.JoinLobbyController;
import com.mygdx.game.doodleMain;

public class JoinLobbyView implements Screen {
    private final doodleMain game;
    private final Stage stage;
    private final Skin skin;
    private final String lobbyCode;
    private Label statusLabel;
    private JoinLobbyController controller;  // Added controller field

    public JoinLobbyView(doodleMain game, String lobbyCode) {
        this.game = game;
        this.lobbyCode = lobbyCode;
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.controller = new JoinLobbyController(game, this, lobbyCode);  // Initialize controller

        createUI();
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Lobby Code Display
        Label codeLabel = new Label("Joined Lobby: " + lobbyCode, skin);
        codeLabel.setAlignment(Align.center);
        codeLabel.setFontScale(2.5f);
        codeLabel.setColor(Color.CYAN);

        // Status Label
        statusLabel = new Label("Connecting...", skin);
        statusLabel.setAlignment(Align.center);
        statusLabel.setFontScale(2.0f);

        // Leave Button
        TextButton leaveBtn = new TextButton("Leave Lobby", skin);
        leaveBtn.getLabel().setFontScale(2.0f);
        leaveBtn.setColor(Color.RED);
        leaveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getFirebaseService().leaveLobby(lobbyCode, game.getPlayerName());
                game.setScreen(new MainMenuView(game));
            }
        });

        // Layout
        mainTable.add(codeLabel).padBottom(40).row();
        mainTable.add(statusLabel).padBottom(60).row();
        mainTable.add(leaveBtn).width(400).height(80);

        // Simulate successful join after 1 second
        Gdx.app.postRunnable(() -> {
            statusLabel.setText("Waiting for host to start...");
            statusLabel.setColor(Color.GREEN);
        });
    }

    public void returnToMainMenu() {
        Gdx.app.postRunnable(() -> game.setScreen(new MainMenuView(game)));
    }

    public void showStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setColor(Color.GREEN);
    }

    public void showError(String message) {
        statusLabel.setText("Error: " + message);
        statusLabel.setColor(Color.RED);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}