package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.doodleMain;

public class TutorialView implements Screen {
    private doodleMain game;
    private Stage stage;
    private Skin skin;

    public TutorialView(doodleMain game){
        this.game = game;
        stage = new Stage(new ScreenViewport());
        //Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        game.openTutorial();

        //Texture videoTexture = new Texture(Gdx.files.internal("example-video-frame.png"));
        //videoWidget = new VideoWidget(videoTexture);
        //table.add(videoWidget).width(800).height(450).row();


        //TextButton backButton = new TextButton("Back", skin);
        //backButton.addListener(event -> {
        //    game.setScreen(new MainMenuView(game));
        //    return true;
        //});
        //table.add(backButton).padTop(20);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
