package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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

    private final float screenWidth  = Gdx.graphics.getWidth();
    private final float screenHeight = Gdx.graphics.getHeight();
    private final float btnWidth     = screenWidth * 0.7f;
    private final float btnHeight    = screenHeight * 0.12f;
    private final float fontScale    = screenHeight / 720f;
    private final float pad          = screenHeight * 0.05f;

    public MainMenuView(doodleMain game) {
        this.game       = game;
        this.controller = new MainMenuController(game, this);
        this.stage      = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        buildUI();
    }

    private void buildUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(pad);
        stage.addActor(table);

        TextButton createBtn = new TextButton("Create Lobby", skin);
        createBtn.getLabel().setFontScale(fontScale);
        createBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                controller.handleCreateLobby();
            }
        });

        TextButton joinBtn = new TextButton("Join Lobby", skin);
        joinBtn.getLabel().setFontScale(fontScale);
        joinBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                controller.handleJoinLobby();
            }
        });

        TextButton tutorialBtn = new TextButton("Tutorial", skin);
        tutorialBtn.getLabel().setFontScale(fontScale);
        tutorialBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                controller.handleTutorial();
            }
        });

        table.add(createBtn).width(btnWidth).height(btnHeight).pad(pad).row();
        table.add(joinBtn)  .width(btnWidth).height(btnHeight).pad(pad).row();
        table.add(tutorialBtn).width(btnWidth).height(btnHeight).pad(pad);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f,0.5f,0.5f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    public void showNameInputDialog(final boolean isHost) {
        final TextField nameField = new TextField("", skin);
        final TextField codeField = new TextField("", skin);
        nameField.getStyle().font.getData().setScale(fontScale);
        codeField.getStyle().font.getData().setScale(fontScale);

        Dialog d = new Dialog(isHost ? "Create Lobby" : "Join Lobby", skin) {
            @Override protected void result(Object object) {
                if (Boolean.TRUE.equals(object)) {
                    if (isHost)    controller.createLobbyWithName(nameField.getText());
                    else           controller.joinLobbyWithName(nameField.getText(), codeField.getText());
                }
            }
        };

        Table content = d.getContentTable();
        content.pad(pad/2);
        content.defaults().width(btnWidth).height(btnHeight/2).pad(pad/4);

        Label nameLabel = new Label("Enter your name:", skin);
        nameLabel.setFontScale(fontScale);
        content.add(nameLabel).row();
        content.add(nameField).row();

        if (!isHost) {
            Label codeLabel = new Label("Enter lobby code:", skin);
            codeLabel.setFontScale(fontScale);
            content.add(codeLabel).row();
            content.add(codeField).row();
        }

        d.getButtonTable().pad(pad/4);
        d.button("OK",  true);
        d.button("Cancel", false);

        d.show(stage);
        d.setPosition(
            (screenWidth  - d.getWidth())  / 2,
            (screenHeight - d.getHeight()) / 2
        );
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
