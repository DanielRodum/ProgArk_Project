package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.controller.CreateLobbyController;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.mygdx.game.doodleMain;

import java.util.Random;

public class CreateLobbyView implements Screen {
    private doodleMain game;
    private Stage stage;
    private Skin skin;
    private CreateLobbyController controller;
    private String lobbyCode;
    private Label codeLabel;
    private Table playerListTable;
    private Table playerContainer;
    private ScrollPane playerListScrollPane;
    private Array<String> players;
    private TextButton startGameBtn;
    private SelectBox<String> wordsetSelectBox;
    private String selectedWordset = "Default Wordset";

    // Used to simulate players joining
    private int nextPlayerNumber = 2;
    private Random random = new Random();

    public CreateLobbyView(doodleMain game, String lobbyCode) {
        this.game = game;
        this.lobbyCode = lobbyCode;
        this.players = new Array<>();
        this.controller = new CreateLobbyController(game, this);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        createUI();

        // Adds host player to the top of the list before any other players
        addPlayer("usernameOfHost (Host)");

        // Simulates players joining
        scheduleExamplePlayers();
    }

    /**
     * Example of players joining after random delays
     */
    private void scheduleExamplePlayers() {
        schedulePlayerJoin(2 + random.nextFloat() * 5);
        schedulePlayerJoin(7 + random.nextFloat() * 5);
        schedulePlayerJoin(12 + random.nextFloat() * 5);
        schedulePlayerJoin(17 + random.nextFloat() * 5);
    }

    private void schedulePlayerJoin(float delaySeconds) {
        final int playerNum = nextPlayerNumber++;
        Timer.schedule(new Task() {
            @Override
            public void run() {
                addPlayer("ExamplePlayer " + playerNum);
            }
        }, delaySeconds);
    }

    private void createUI() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        codeLabel = new Label("Code: " + lobbyCode, skin);
        codeLabel.setAlignment(Align.center);
        codeLabel.setFontScale(4.0f);
        codeLabel.setColor(Color.GREEN);

        Label playersHeaderLabel = new Label("Waiting for Doodlers...", skin);
        playersHeaderLabel.setAlignment(Align.center);
        playersHeaderLabel.setFontScale(3.0f);
        playersHeaderLabel.setColor(Color.GREEN);

        playerContainer = new Table();
        playerContainer.setBackground(skin.newDrawable("white"));

        playerListTable = new Table();
        playerListTable.defaults().pad(15).expandX().fillX();

        playerListScrollPane = new ScrollPane(playerListTable, skin);
        playerListScrollPane.setFadeScrollBars(false);

        playerContainer.add(playerListScrollPane).expand().fill().pad(15);

        wordsetSelectBox = new SelectBox<>(skin);
        wordsetSelectBox.setItems("Default Wordset", "Funny words only", "Crazy words!!!");
        wordsetSelectBox.setSelected("Default Wordset");

        wordsetSelectBox.setColor(Color.WHITE);
        wordsetSelectBox.getStyle().listStyle.font.getData().setScale(3.0f);
        wordsetSelectBox.getStyle().font.getData().setScale(3.0f);

        Table wordsetContainer = new Table();
        wordsetContainer.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 0.9f)));

        Table wordsetTable = new Table();
        Label wordsetLabel = new Label("Select Wordset:", skin);
        wordsetLabel.setFontScale(3.5f);
        wordsetLabel.setColor(Color.WHITE);

        wordsetTable.add(wordsetLabel).padRight(20);
        wordsetTable.add(wordsetSelectBox).width(450).height(100);

        wordsetContainer.add(wordsetTable).pad(20);

        SelectBox.SelectBoxStyle style = wordsetSelectBox.getStyle();
        style.listStyle.selection = skin.newDrawable("white", new Color(0.2f, 0.7f, 0.2f, 1));

        wordsetSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedWordset = wordsetSelectBox.getSelected();
                System.out.println("Selected wordset: " + selectedWordset);
            }
        });

        startGameBtn = new TextButton("Start Game", skin);
        startGameBtn.getLabel().setFontScale(3.0f);
        startGameBtn.setColor(Color.GREEN);
        startGameBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.handleStartGame();
            }
        });

        mainTable.add(codeLabel).pad(30).expandX().fillX();
        mainTable.row();
        mainTable.add(playersHeaderLabel).pad(15).expandX().fillX();
        mainTable.row();
        mainTable.add(playerContainer).expand().fill().maxHeight(Gdx.graphics.getHeight() * 0.32f).pad(15);
        mainTable.row();
        mainTable.add(wordsetContainer).fillX().padTop(5).padBottom(30);
        mainTable.row();
        mainTable.add(startGameBtn).pad(10).width(500).height(150);
    }

    public void addPlayer(String playerName) {
        if (!playerNameExists(playerName)) {
            players.add(playerName);
            updatePlayerList();
        }
    }

    private boolean playerNameExists(String playerName) {
        for (String player : players) {
            if (player.equals(playerName)) {
                return true;
            }
        }
        return false;
    }

    public void removePlayer(String playerName) {
        players.removeValue(playerName, false);
        updatePlayerList();
    }

    private void updatePlayerList() {
        playerListTable.clear();
        int hostIndex = -1;
        for (int i = 0; i < players.size; i++) {
            if (players.get(i).contains("(Host)")) {
                hostIndex = i;
                break;
            }
        }

        if (hostIndex != -1) {
            Label hostLabel = new Label("1. " + players.get(hostIndex), skin);
            hostLabel.setFontScale(3.5f);
            hostLabel.setColor(Color.BLACK);
            hostLabel.setAlignment(Align.center);
            playerListTable.add(hostLabel).expandX().fillX().padTop(20);
            playerListTable.row();
        }

        int displayIndex = 2;
        for (int i = 0; i < players.size; i++) {
            if (i == hostIndex)
                continue;

            String player = players.get(i);
            Label playerLabel = new Label(displayIndex + ". " + player, skin);
            playerLabel.setFontScale(3.5f);
            playerLabel.setColor(Color.BLACK);
            playerLabel.setAlignment(Align.center);
            playerListTable.add(playerLabel).expandX().fillX().padTop(20);
            playerListTable.row();
            displayIndex++;
        }
    }

    /**
     * Get the currently selected wordset
     *
     * @return The name of the selected wordset
     */
    public String getSelectedWordset() {
        return selectedWordset;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
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
    }
}
