package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.List;

public class ChooseWordView implements Screen {
    private final doodleMain game;
    private final Stage stage;
    private final Skin skin;

    public ChooseWordView(doodleMain game){
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        Gdx.input.setInputProcessor(stage);

        Label loadingLabel = new Label("Loading words...", skin);
        loadingLabel.setPosition(Gdx.graphics.getWidth()/2f-loadingLabel.getWidth()/2f, Gdx.graphics.getHeight()/2f);
        stage.addActor(loadingLabel);

        fetchAndDisplayWords();
    }

    private void fetchAndDisplayWords(){
        FirebaseInterface firebase = game.getFirebaseService();
        if (firebase != null) {
            firebase.fetchWords(new FirebaseInterface.FirestoreCallback() {
                @Override
                public void onSuccess(List<String> words) {
                    Gdx.app.postRunnable(() -> displayWords(words));
                }

                @Override
                public void onFailure(Exception e) {
                    Gdx.app.postRunnable(() -> {
                        stage.clear();
                        Label error = new Label("Failed to load words", skin);
                        error.setFontScale(2);
                        error.setPosition(Gdx.graphics.getWidth() / 2f - error.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
                        stage.addActor(error);
                    });
                }
            });
        }
    }

    private void displayWords(List<String> words){
        stage.clear();

        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(50);
        stage.addActor(table);

        Label title = new Label("Choose a Word", skin);
        table.add(title).colspan(2).padBottom(30);
        table.row();

        for (String word : words){
            TextButton button = new TextButton(word, skin);
            button.addListener(new ClickListener(){
                @Override
                public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y){
                    Gdx.app.log("ChooseWord", "Selected word: " + word);
                    //TODO: transition to next screen or action
                }
            });
            table.add(button).width(200).height(50).pad(10);
            table.row();
        }
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
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
