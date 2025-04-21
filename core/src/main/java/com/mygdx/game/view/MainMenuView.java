package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.controller.MainMenuController;
import com.mygdx.game.doodleMain;

public class MainMenuView implements Screen {
    private doodleMain game;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Skin skin;
    private MainMenuController controller;

    public MainMenuView(doodleMain game){
        this.game = game;
        this.controller = new MainMenuController(game, this);
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();
        font = new BitmapFont();

        createUI();
    }

    private void createUI() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        float screenWidth  = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float btnWidth  = screenWidth  * 0.7f;
        float btnHeight = screenHeight * 0.12f;
        float fontScale = screenHeight / 720f;
        float pad       = screenHeight * 0.05f;

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(screenHeight * 0.1f);
        stage.addActor(table);

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
        joinLobbyBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                controller.handleJoinLobby();
            }
        });
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
    public void render(float delta){
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    public void showNameInputDialog(boolean isHost) {
        TextField nameField = new TextField("", skin);
        TextField codeField = isHost ? null : new TextField("", skin);

        Dialog dialog = new Dialog(isHost ? "Create Lobby" : "Join Lobby", skin) {
            protected void result(Object object) {
                if (object.equals(true)) {
                    if (isHost) {
                        controller.createLobbyWithName(nameField.getText());
                    } else {
                        controller.joinLobbyWithName(nameField.getText(), codeField.getText());
                    }
                }
            }
        };

        dialog.getContentTable().add(new Label("Enter your name:", skin)).pad(10);
        dialog.getContentTable().add(nameField).width(300).pad(10).row();

        if (!isHost) {
            dialog.getContentTable().add(new Label("Enter lobby code:", skin)).pad(10);
            dialog.getContentTable().add(codeField).width(300).pad(10).row();
        }

        dialog.button("OK", true);
        dialog.button("Cancel", false);
        dialog.show(stage);
    }

    public void showJoinDialog() {
        showNameInputDialog(false);
    }

    public void showError(String message) {
        new Dialog("Error", skin) {
            {
                text(message);
                button("OK");
            }
        }.show(stage);
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void show(){}

    @Override
    public void hide (){}

    @Override
    public void pause(){}

    @Override
    public void resume(){}

    @Override
    public void dispose(){
        stage.dispose();
        batch.dispose();
        font.dispose();
        skin.dispose();
    }
}
