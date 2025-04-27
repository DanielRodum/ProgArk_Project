package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.controller.gamecontrollers.DrawingController;
import com.mygdx.game.doodleMain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class DrawingView implements Screen {
    private final doodleMain game;
    private final Stage uiStage;
    private final Viewport viewport;
    private final ShapeRenderer shapeRenderer;
    private final List<Stroke> strokes = new ArrayList<>();
    private Color currentColor = Color.BLACK;

    private final DrawingController controller;

    private Label wordLabel;
    private Label timerLabel;
    private final List<Texture> colorTextures = new ArrayList<>();

    public DrawingView(doodleMain game, String lobbyCode){
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        this.viewport = new FitViewport(1080, 1920, camera);
        viewport.apply();
        this.uiStage = new Stage(viewport);
        this.shapeRenderer = new ShapeRenderer();

        setupUI();

        Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, createDrawingInputProcessor()));
        this.controller = new DrawingController(game, this, lobbyCode);
    }

    private void setupUI(){
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(20);
        uiStage.addActor(table);

        wordLabel = new Label("Word: loading...", skin);
        wordLabel.setColor(Color.BLACK);
        wordLabel.setFontScale(5f);

        timerLabel = new Label("30", skin);
        timerLabel.setColor(Color.BLACK);
        timerLabel.setFontScale(5f);

        table.add(wordLabel).expandX().left().padLeft(20);
        table.add(timerLabel).expandX().right().padRight(20);
        table.row().padTop(10);

        Table colorButtons = new Table();
        addColorButton(colorButtons, Color.BLACK);
        addColorButton(colorButtons, Color.RED);
        addColorButton(colorButtons, Color.GREEN);
        addColorButton(colorButtons, Color.BLUE);
        addColorButton(colorButtons, Color.YELLOW);
        addColorButton(colorButtons, Color.PINK);
        addColorButton(colorButtons, Color.PURPLE);
        addColorButton(colorButtons, Color.ORANGE);
        addColorButton(colorButtons, Color.WHITE);

        table.add(colorButtons).colspan(2).center().padTop(20);
    }

    private void addColorButton(Table table, Color color){
        int size = 100;
        int borderWidth = 3;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        pixmap.setColor(color);
        pixmap.fillRectangle(borderWidth, borderWidth, size-2*borderWidth, size-2 * borderWidth);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        colorTextures.add(texture);

        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = drawable;

        ImageButton btn = new ImageButton(style);
        btn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                currentColor = new Color(color);
            }
        });
        table.add(btn).size(size).pad(10);
    }

    private InputProcessor createDrawingInputProcessor() {
        return new InputAdapter() {
            private Stroke currentStroke = null;
            private String currentStrokeId = null;

            @Override
            public boolean touchDown(int sx, int sy, int ptr, int button) {
                Vector2 p = viewport.unproject(new Vector2(sx, sy));
                currentStroke = new Stroke(currentColor);
                currentStrokeId = UUID.randomUUID().toString();
                currentStroke.addPoint(p);
                strokes.add(currentStroke);
                // send initial stroke point to guessers
                controller.onLocalStrokeCompleted(currentStrokeId,
                    new ArrayList<>(currentStroke.getPoints()),
                    currentStroke.getColor());
                return true;
            }

            @Override
            public boolean touchDragged(int sx, int sy, int ptr) {
                if (currentStroke != null) {
                    Vector2 p = viewport.unproject(new Vector2(sx, sy));
                    currentStroke.addPoint(p);
                    // send updated stroke data for real-time rendering
                    controller.onLocalStrokeCompleted(currentStrokeId,
                        new ArrayList<>(currentStroke.getPoints()),
                        currentStroke.getColor());
                }
                return true;
            }

            @Override
            public boolean touchUp(int sx, int sy, int ptr, int button) {
                // clear stroke state on lift
                currentStroke = null;
                currentStrokeId = null;
                return true;
            }
        };
    }

    public void setWord(String word){
        if (wordLabel!=null){
            wordLabel.setText("Word: "+word);
        }
    }

    private static class Stroke{
        private final List<Vector2> points = new ArrayList<>();
        private final Color color;
        public Stroke(Color color){
            this.color = new Color(color);
        }

        public void addPoint(Vector2 point){
            points.add(point);
        }

        public void render(ShapeRenderer renderer){
            renderer.setColor(color);
            for (int i=1; i<points.size(); i++){
                Vector2 p1 = points.get(i-1);
                Vector2 p2 = points.get(i);
                renderer.rectLine(p1.x, p1.y, p2.x, p2.y, 10f);
            }
        }

        public List<Vector2> getPoints() {
            return points;
        }

        public Color getColor() {
            return color;
        }
    }

    public void addRemoteStroke(List<Vector2> pts, Color color) {
        Stroke s = new Stroke(color);
        for (Vector2 p : pts) {
            s.addPoint(p);
        }
        strokes.add(s);
    }


    public void setTime(int seconds){
        timerLabel.setText(String.valueOf(seconds));
    }

    @Override
    public void show() {
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Stroke stroke : strokes){
            stroke.render(shapeRenderer);
        }
        shapeRenderer.end();

        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        uiStage.dispose();
        shapeRenderer.dispose();
        for (Texture t : colorTextures) t.dispose();
    }
}
