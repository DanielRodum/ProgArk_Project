package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.controller.MainMenuController;
import com.mygdx.game.doodleMain;

public class MainMenuView implements Screen {
    private final doodleMain game;
    private final Stage stage;
    private final Skin skin;
    private final MainMenuController controller;

    public MainMenuView(doodleMain game) {
        this.game = game;
        this.controller = new MainMenuController(game, this);
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        buildUI();
    }

        float screenWidth  = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float btnWidth  = screenWidth  * 0.7f;
        float btnHeight = screenHeight * 0.12f;
        float fontScale = screenHeight / 720f;
        float pad       = screenHeight * 0.05f;

    private void buildUI() {
        Table t = new Table();
        t.setFillParent(true);
        stage.addActor(t);
        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(screenHeight * 0.1f);
        stage.addActor(table);

        TextButton createBtn = new TextButton("Create Lobby", skin);
        createBtn.getLabel().setFontScale(2f);
        createBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
        TextButton createLobbyBtn = new TextButton("Create Lobby", skin);
        TextButton joinLobbyBtn   = new TextButton("Join Lobby",   skin);
        TextButton tutorialBtn    = new TextButton("Tutorial",     skin);

        createLobbyBtn.getLabel().setFontScale(fontScale);
        joinLobbyBtn  .getLabel().setFontScale(fontScale);
        tutorialBtn   .getLabel().setFontScale(fontScale);

        createLobbyBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                controller.handleCreateLobby();
            }
        });

        TextButton joinBtn = new TextButton("Join Lobby", skin);
        joinBtn.getLabel().setFontScale(2f);
        joinBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                controller.handleJoinLobby();
            }
        });

        t.add(createBtn).width(300).height(60).pad(20).row();
        t.add(joinBtn).width(300).height(60).pad(20).row();
        tutorialBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                controller.handleTutorial();
            }
        });

        table.add(createLobbyBtn).width(btnWidth).height(btnHeight).pad(pad).row();
        table.add(joinLobbyBtn)  .width(btnWidth).height(btnHeight).pad(pad).row();
        table.add(tutorialBtn)   .width(btnWidth).height(btnHeight).pad(pad);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f,0f,0f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    /** Pops up name (and code) dialog. */
    public void showNameInputDialog(final boolean isHost) {
        final TextField nameField = new TextField("", skin);
        final TextField codeField = new TextField("", skin);

        Dialog d = new Dialog(isHost ? "Create Lobby" : "Join Lobby", skin) {
            @Override protected void result(Object obj) {
                if (Boolean.TRUE.equals(obj)) {
                    if (isHost) controller.createLobbyWithName(nameField.getText());
                    else        controller.joinLobbyWithName(nameField.getText(), codeField.getText());
                }
            }
        };

        Table c = d.getContentTable();
        c.add(new Label("Enter your name:", skin)).pad(10);
        c.add(nameField).width(300).pad(10).row();
        if (!isHost) {
            c.add(new Label("Enter lobby code:", skin)).pad(10);
            c.add(codeField).width(300).pad(10).row();
        }

        d.button("OK", true);
        d.button("Cancel", false);
        d.show(stage);
    }

    public void showError(String msg) {
        Dialog d = new Dialog("Error", skin);
        d.text(msg);
        d.button("OK");
        d.show(stage);
    }

    @Override public void resize(int w,int h) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
