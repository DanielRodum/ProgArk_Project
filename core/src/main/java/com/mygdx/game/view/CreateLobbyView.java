package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
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
import com.mygdx.game.doodleMain;

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


    public CreateLobbyView(doodleMain game, String lobbyCode) {
        this.game = game;
        this.lobbyCode = lobbyCode;
        this.players = new Array<>();
        this.controller = new CreateLobbyController(game, this, lobbyCode);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        createUI();
    }

    private void createUI() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Lobby Code Display
        codeLabel = new Label("Code: " + lobbyCode, skin);
        codeLabel.setAlignment(Align.center);
        codeLabel.setFontScale(4.0f);
        codeLabel.setColor(Color.GREEN);

        // Player List Header
        Label playersHeaderLabel = new Label("Waiting for Players...", skin);
        playersHeaderLabel.setAlignment(Align.center);
        playersHeaderLabel.setFontScale(3.0f);
        playersHeaderLabel.setColor(Color.GREEN);

        // Player List Container
        playerContainer = new Table();
        playerContainer.setBackground(skin.newDrawable("white"));

        playerListTable = new Table();
        playerListTable.defaults().pad(15).expandX().fillX();

        playerListScrollPane = new ScrollPane(playerListTable, skin);
        playerListScrollPane.setFadeScrollBars(false);
        playerContainer.add(playerListScrollPane).expand().fill().pad(15);

        // Wordset Selection
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

        // Wordset selection listener
        wordsetSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedWordset = wordsetSelectBox.getSelected();
                Gdx.app.log("Lobby", "Selected wordset: " + selectedWordset);
            }
        });

        // Start Game Button
        startGameBtn = new TextButton("Start Game", skin);
        startGameBtn.getLabel().setFontScale(3.0f);
        startGameBtn.setColor(Color.GREEN);
        startGameBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.handleStartGame();
            }
        });

        // Main Layout
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

    public void setLobbyCode(String code) {
        this.lobbyCode = code;
        codeLabel.setText("Code: " + code);
    }

    public void showError(String message) {
        Dialog errorDialog = new Dialog("Error", skin) {
            {
                text(message);
                button("OK");
                getContentTable().pad(20);
                getButtonTable().padBottom(15);
            }
        };
        errorDialog.show(stage);

        // Make text larger
        Label label = (Label)errorDialog.getContentTable().getChildren().peek();
        label.setFontScale(2f);

        // Style the button
        TextButton button = (TextButton)errorDialog.getButtonTable().getChildren().peek();
        button.getLabel().setFontScale(2f);
    }

    public void showStatus(String message) {
        // Create a temporary status label that appears above the player list
        Label statusLabel = new Label(message, skin);
        statusLabel.setFontScale(2.5f);
        statusLabel.setColor(Color.YELLOW);

        // Clear any existing status messages
        clearStatusMessages();

        // Add to player list table at the top
        playerListTable.add(statusLabel).expandX().fillX().padBottom(20).row();
        playerListTable.invalidateHierarchy();
    }

    private void clearStatusMessages() {
        // Remove any existing status labels (they'll be the first children if they exist)
        while (playerListTable.getChildren().size > 0 &&
            playerListTable.getChildren().first() instanceof Label) {
            playerListTable.getChildren().removeIndex(0);
        }
    }

    public void addPlayer(String playerName) {
        if (!playerNameExists(playerName)) {
            players.add(playerName);
            updatePlayerList();
        }
    }

    public void removePlayer(String playerName) {
        players.removeValue(playerName, false);
        updatePlayerList();
    }

    public void clearPlayers() {
        players.clear();
        playerListTable.clear();
    }

    private void updatePlayerList() {
        playerListTable.clear();

        // Sort players with host first
        Array<String> sortedPlayers = new Array<>(players);
        sortedPlayers.sort((p1, p2) -> {
            boolean p1IsHost = p1.contains("(Host)");
            boolean p2IsHost = p2.contains("(Host)");
            if (p1IsHost && !p2IsHost) return -1;
            if (!p1IsHost && p2IsHost) return 1;
            return p1.compareTo(p2);
        });

        // Display players with numbering
        for (int i = 0; i < sortedPlayers.size; i++) {
            String player = sortedPlayers.get(i);
            Label playerLabel = new Label((i+1) + ". " + player, skin);
            playerLabel.setFontScale(3.5f);
            playerLabel.setColor(Color.BLACK);
            playerLabel.setAlignment(Align.center);

            if (player.contains("(Host)")) {
                playerLabel.setColor(Color.GREEN);
            }

            playerListTable.add(playerLabel).expandX().fillX().padTop(20);
            playerListTable.row();
        }
    }

    public Array<String> getPlayers() {
        return new Array<>(players); // Return a copy of the players array
    }

    public boolean playerNameExists(String name) {
        for (String player : players) {
            if (player.equals(name) || player.startsWith(name + " (")) {
                return true;
            }
        }
        return false;
    }

    public void returnToMainMenu() {
        game.setScreen(new MainMenuView(game));
    }


    @Override public void render(float delta) {}
    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
