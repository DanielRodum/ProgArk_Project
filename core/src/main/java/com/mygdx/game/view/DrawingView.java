package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.doodleMain;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class DrawingView implements Screen {
    private final doodleMain game;
    private final Stage stage;
    private final Skin skin;
    private final String word;

    // Drawing related fields
    private Pixmap drawingPixmap;
    private Texture drawingTexture;
    private SpriteBatch batch;
    private Vector2 lastTouchPos;
    private Color currentColor;
    private int brushSize = 5;

    // Drawing area dimensions
    private final int CANVAS_WIDTH = 800;
    private final int CANVAS_HEIGHT = 600;
    private final int CANVAS_X;
    private final int CANVAS_Y;

    public DrawingView(doodleMain game, String word) {
        this.game = game;
        this.word = word;
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Center the canvas
        CANVAS_X = (Gdx.graphics.getWidth() - CANVAS_WIDTH) / 2;
        CANVAS_Y = (Gdx.graphics.getHeight() - CANVAS_HEIGHT) / 2;

        setupUI();
        setupDrawing();

        Gdx.input.setInputProcessor(stage);
    }

    private void setupUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Word display at the top
        Label wordLabel = new Label("Draw: " + word, skin);
        wordLabel.setFontScale(2.0f);

        // Color selection buttons
        Table colorTable = new Table();
        addColorButton(colorTable, Color.BLACK);
        addColorButton(colorTable, Color.BLUE);
        addColorButton(colorTable, Color.RED);
        addColorButton(colorTable, Color.YELLOW);
        addColorButton(colorTable, Color.GREEN);

        mainTable.add(wordLabel).padBottom(20);
        mainTable.row();
        mainTable.add(colorTable).padBottom(10);

        stage.addActor(mainTable);
    }

    private void addColorButton(Table table, final Color color) {
        TextButton button = new TextButton(" ", skin);
        button.setColor(color);
        button.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                currentColor = color;
            }
        });
        table.add(button).size(40).pad(5);
    }

    private void setupDrawing() {
        drawingPixmap = new Pixmap(CANVAS_WIDTH, CANVAS_HEIGHT, Pixmap.Format.RGBA8888);
        drawingPixmap.setColor(Color.WHITE);
        drawingPixmap.fill();

        drawingTexture = new Texture(drawingPixmap);
        batch = new SpriteBatch();
        lastTouchPos = new Vector2();
        currentColor = Color.BLACK;
    }

    private void handleDrawing() {
        if (Gdx.input.isTouched()) {
            Vector2 touchPos = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

            // Check if touch is within canvas bounds
            if (isWithinCanvas(touchPos)) {
                // Convert to canvas coordinates
                Vector2 canvasPos = new Vector2(
                        touchPos.x - CANVAS_X,
                        touchPos.y - CANVAS_Y);

                if (lastTouchPos.x > 0 && lastTouchPos.y > 0) {
                    drawingPixmap.setColor(currentColor);
                    drawLine(
                            (int) lastTouchPos.x,
                            (int) lastTouchPos.y,
                            (int) canvasPos.x,
                            (int) canvasPos.y);
                }

                lastTouchPos.set(canvasPos);
                drawingTexture.draw(drawingPixmap, 0, 0);
            }
        } else {
            lastTouchPos.set(-1, -1);
        }
    }

    private boolean isWithinCanvas(Vector2 pos) {
        return pos.x >= CANVAS_X && pos.x < CANVAS_X + CANVAS_WIDTH &&
                pos.y >= CANVAS_Y && pos.y < CANVAS_Y + CANVAS_HEIGHT;
    }

    private void drawLine(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            drawCircle(x1, y1, brushSize);

            if (x1 == x2 && y1 == y2)
                break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x1 = x1 + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
        }
    }

    private void drawCircle(int centerX, int centerY, int radius) {
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                if (x * x + y * y <= radius * radius) {
                    int px = centerX + x;
                    int py = centerY + y;
                    if (px >= 0 && px < CANVAS_WIDTH && py >= 0 && py < CANVAS_HEIGHT) {
                        drawingPixmap.drawPixel(px, py);
                    }
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleDrawing();

        batch.begin();
        batch.draw(drawingTexture, CANVAS_X, CANVAS_Y);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        drawingPixmap.dispose();
        drawingTexture.dispose();
        batch.dispose();
    }
}