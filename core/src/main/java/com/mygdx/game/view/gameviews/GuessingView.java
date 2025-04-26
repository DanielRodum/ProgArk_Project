package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.doodleMain;

import java.util.ArrayList;
import java.util.List;

/**
 * View for guessers: shows masked word, timer, drawing canvas, and input.
 */
public class GuessingView implements Screen {

    /** Listener for when the user submits a guess */
    public interface GuessListener {
        void onGuess(String guess);
    }

    private final doodleMain game;
    private GuessListener onGuess;

    private Stage stage;
    private Skin skin;
    private Label maskedWordLabel;
    private Label timerLabel;
    private TextField guessField;
    private TextButton submitBtn;
    private Label feedbackLabel;

    private ShapeRenderer shapeRenderer;
    private final List<Stroke> strokes = new ArrayList<>();

    public GuessingView(doodleMain game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        this.shapeRenderer = new ShapeRenderer();
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
            .padBottom(10f)
            .row();

        timerLabel = new Label("60", skin);
        timerLabel.setFontScale(1.2f);
        timerLabel.setColor(Color.RED);
        timerLabel.setAlignment(Align.center);
        table.add(timerLabel)
            .width(Gdx.graphics.getWidth() * 0.8f)
            .padBottom(20f)
            .row();

        guessField = new TextField("", skin);
        guessField.setMessageText("Your guess…");
        table.add(guessField)
            .width(Gdx.graphics.getWidth() * 0.6f)
            .padBottom(20f)
            .row();

        submitBtn = new TextButton("Submit", skin);
        submitBtn.getLabel().setFontScale(1.2f);
        table.add(submitBtn)
            .width(Gdx.graphics.getWidth() * 0.4f)
            .height(Gdx.graphics.getHeight() * 0.1f)
            .padBottom(20f)
            .row();

        feedbackLabel = new Label("", skin);
        feedbackLabel.setFontScale(1.2f);
        feedbackLabel.setAlignment(Align.center);
        table.add(feedbackLabel)
            .width(Gdx.graphics.getWidth() * 0.8f)
            .row();
    }

    /**
     * Assigns the listener and wires up the submit button.
     */
    public void setGuessListener(GuessListener listener) {
        this.onGuess = listener;
        submitBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (onGuess != null) {
                    onGuess.onGuess(guessField.getText());
                    guessField.setText("");
                }
            }
        });
    }

    public void displayMaskedWord(String masked) {
        maskedWordLabel.setText(masked);
    }

    public void setTime(int seconds) {
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

    /** Called by controller to render strokes from the drawer in real time */
    public void addRemoteStroke(List<Vector2> pts, Color color) {
        Stroke s = new Stroke(color);
        for (Vector2 p : pts) s.addPoint(p);
        strokes.add(s);
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Stroke s : strokes) s.render(shapeRenderer);
        shapeRenderer.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void show() {}
    @Override public void hide() {}

    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
        shapeRenderer.dispose();
    }

    // Internal stroke representation for rendering
    private static class Stroke {
        private final List<Vector2> points = new ArrayList<>();
        private final Color color;
        public Stroke(Color color) { this.color = new Color(color); }
        public void addPoint(Vector2 p) { points.add(p); }
        public void render(ShapeRenderer r) {
            r.setColor(color);
            for (int i = 1; i < points.size(); i++) {
                Vector2 a = points.get(i - 1);
                Vector2 b = points.get(i);
                r.rectLine(a.x, a.y, b.x, b.y, 10f);
            }
        }
    }
}
