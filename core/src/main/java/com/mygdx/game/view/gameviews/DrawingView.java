package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.controller.gamecontrollers.DrawingController;
import com.mygdx.game.doodleMain;

import java.util.ArrayList;
import java.util.List;

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

    public DrawingView(doodleMain game, String lobbyCode){
        this.game = game;
        this.viewport = new FitViewport(1080, 1920, new OrthographicCamera());
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
        wordLabel.setColor(Color.WHITE);
        wordLabel.setFontScale(2f);

        timerLabel = new Label("60", skin);
        timerLabel.setColor(Color.YELLOW);
        timerLabel.setFontScale(2f);

        table.add(wordLabel).expandX().left().padLeft(20);
        table.add(timerLabel).expandX().right().padRight(20);
        table.row().padTop(10);

        Table colorButtons = new Table();
        addColorButton(colorButtons, Color.BLACK, skin);
        addColorButton(colorButtons, Color.RED, skin);
        addColorButton(colorButtons, Color.GREEN, skin);
        addColorButton(colorButtons, Color.BLUE, skin);

        table.add(colorButtons).colspan(2).center().padTop(20);
    }

    private void addColorButton(Table table, Color color, Skin skin){
        TextButton btn = new TextButton("", skin);
        btn.setColor(color);
        btn.getLabel().setText("");
        btn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                currentColor = new Color(color);
            }
        });
        table.add(btn).size(80).pad(10);
    }

    private InputProcessor createDrawingInputProcessor(){
        return new InputAdapter(){
            private Stroke currentStroke = null;
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button){
                Vector2 touch = viewport.unproject(new Vector2(screenX, screenY));
                currentStroke = new Stroke(currentColor);
                currentStroke.addPoint(touch);
                strokes.add(currentStroke);
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer){
                if (currentStroke != null){
                    Vector2 touch = viewport.unproject(new Vector2(screenX, screenY));
                    currentStroke.addPoint(touch);
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button){
                currentStroke = null;
                return true;
            }
        };
    }

    public void setWord(String word){
        if (wordLabel!=null){
            wordLabel.setText("Word: "+word);
        }
    }

    public void setTime(int seconds){
        timerLabel.setText(String.valueOf(seconds));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
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
    }

    //helper class for strokes
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
                renderer.line(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }
}
