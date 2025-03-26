package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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


        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton createLobbyBtn = new TextButton("Create Lobby", skin, "default");
        TextButton joinLobbyBtn = new TextButton("Join Lobby", skin, "default");
        TextButton tutorialBtn = new TextButton("Tutorial", skin, "default");

        createLobbyBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                controller.handleCreateLobby();
            }
        });

        joinLobbyBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                controller.handleJoinLobby();
            }
        });

        tutorialBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                controller.handleTutorial();
            }
        });

        table.add(createLobbyBtn).width(600).height(50).pad(50);
        table.row();
        table.add(joinLobbyBtn).width(600).height(50).pad(50);
        table.row();
        table.add(tutorialBtn).width(600).height(50).pad(50);
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
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
