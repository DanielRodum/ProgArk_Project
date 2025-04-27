package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.controller.gamecontrollers.GuessingController;
import com.mygdx.game.doodleMain;

import java.util.ArrayList;
import java.util.List;

public class GuessingView implements Screen {
    private final doodleMain game;
    private GuessingController controller;
    private final Stage stage;
    private final Skin skin;

    private Label maskedWordLabel;
    private Label timerLabel;    // <--- new timer label
    private TextField guessField;
    private TextButton submitBtn;
    private Label feedbackLabel;

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final List<Stroke> strokes = new ArrayList<>();

    public GuessingView(doodleMain game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(Gdx.graphics.getHeight() * 0.05f);
        stage.addActor(table);

        maskedWordLabel = new Label("", skin);
        maskedWordLabel.setFontScale(1.5f);
        maskedWordLabel.setAlignment(Align.center);
        table.add(maskedWordLabel)
            .width(Gdx.graphics.getWidth() * 0.8f)
            .padBottom(10f).row();

        timerLabel = new Label("60", skin);    // default 60
        timerLabel.setFontScale(3f);
        timerLabel.setColor(Color.BLACK);
        timerLabel.setAlignment(Align.center);
        table.add(timerLabel)
            .width(Gdx.graphics.getWidth() * 0.8f)
            .padBottom(20f).row();

        guessField = new TextField("", skin);
        guessField.setMessageText("Your guess…");
        //MANGLER Å ØKE FONT HER! TODO
        table.add(guessField)
            .width(Gdx.graphics.getWidth() * 0.7f)
            .height(Gdx.graphics.getHeight() * 0.03f)
            .padBottom(20f).row();

        submitBtn = new TextButton("Submit", skin);
        submitBtn.getLabel().setFontScale(3f);
        table.add(submitBtn)
            .width(Gdx.graphics.getWidth() * 0.7f)
            .height(Gdx.graphics.getHeight() * 0.08f)
            .padBottom(20f).row();

        feedbackLabel = new Label("", skin);
        feedbackLabel.setFontScale(1.8f);
        feedbackLabel.setAlignment(Align.center);
        table.add(feedbackLabel)
            .width(Gdx.graphics.getWidth() * 0.8f).row();
    }

    public void setController(GuessingController controller) {
        this.controller = controller;
        submitBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                controller.onGuessSubmitted(guessField.getText());
                guessField.setText("");
            }
        });
    }

    public void displayMaskedWord(String masked) {
        maskedWordLabel.setText(masked);
    }

    public void setTime(int seconds) {  // <--- update timer label
        timerLabel.setText(String.valueOf(seconds));
    }

    public void showCorrectFeedback() {
        feedbackLabel.setText("Correct!");
        feedbackLabel.setColor(Color.GREEN);
    }

    public void showIncorrectFeedback() {
        feedbackLabel.setText("Try again…");
        feedbackLabel.setColor(Color.RED);
    }

    public void addRemoteStroke(List<Vector2> pts, Color color) {
        Stroke s = new Stroke(color);
        for (Vector2 p : pts) {
            s.addPoint(p);
        }
        strokes.add(s);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Stroke s : strokes) s.render(shapeRenderer);
        shapeRenderer.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w,int h) { stage.getViewport().update(w,h,true);}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); shapeRenderer.dispose(); }

    private static class Stroke {
        private final List<Vector2> points = new ArrayList<>();
        private final Color color;
        public Stroke(Color color) { this.color = new Color(color); }
        public void addPoint(Vector2 p) { points.add(p); }
        public void render(ShapeRenderer r) {
            r.setColor(color);
            for (int i=1; i<points.size(); i++) {
                Vector2 p1 = points.get(i-1), p2 = points.get(i);
                r.rectLine(p1.x,p1.y,p2.x,p2.y,10f);
            }
        }
    }
}
