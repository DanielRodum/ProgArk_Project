package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.controller.MainMenuController;
import com.mygdx.game.doodleMain;

public class MainMenuView implements Screen {
    private final doodleMain game;
    private final Stage stage;
    private final Skin skin;
    private final MainMenuController controller;

    private final float screenWidth  = Gdx.graphics.getWidth();
    private final float screenHeight = Gdx.graphics.getHeight();
    private final float btnWidth     = screenWidth * 0.7f;
    private final float btnHeight    = screenHeight * 0.08f;
    private final float fontScale    = screenHeight / 600f;
    private final float pad          = screenHeight * 0.05f;

    public MainMenuView(doodleMain game) {
        this.game       = game;
        this.controller = new MainMenuController(game, this);
        this.stage      = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        buildUI();
    }

    private void buildUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.top().padTop(pad*2);
        Texture logoTexture = new Texture(Gdx.files.internal("logo.png"));
        Image logoImage = new Image(logoTexture);
        float logoWidth = screenWidth * 0.9f;
        float logoHeight = logoTexture.getHeight() * (logoWidth / logoTexture.getWidth());
        stage.addActor(table);

        TextButton createBtn = new TextButton("Create Lobby", skin);
        createBtn.getLabel().setFontScale(fontScale);
        createBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                controller.handleCreateLobby();
            }
        });

        TextButton joinBtn = new TextButton("Join Lobby", skin);
        joinBtn.getLabel().setFontScale(fontScale);
        joinBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                controller.handleJoinLobby();
            }
        });

        TextButton tutorialBtn = new TextButton("Tutorial", skin);
        tutorialBtn.getLabel().setFontScale(fontScale);
        tutorialBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                controller.handleTutorial();
            }
        });

        float smallerPad = pad * 0.4f;
        table.add(logoImage)
            .width(logoWidth)
            .height(logoHeight)
            .padBottom(pad)
            .center()
            .row();

        table.add(createBtn).width(btnWidth).height(btnHeight).pad(smallerPad).row();
        table.add(joinBtn)  .width(btnWidth).height(btnHeight).pad(smallerPad).row();
        table.add(tutorialBtn).width(btnWidth).height(btnHeight).pad(smallerPad);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f,0.5f,0.5f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    public void showNameInputDialog(final boolean isHost) {
        final TextField nameField = new TextField("", skin);
        final TextField codeField = new TextField("", skin);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("font");
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.background = skin.newDrawable("white", Color.WHITE); // white input box
        textFieldStyle.cursor = skin.newDrawable("white", Color.BLACK); // visible cursor
        textFieldStyle.selection = skin.newDrawable("white", new Color(0.3f, 0.3f, 1f, 0.4f)); // selection color
        if (textFieldStyle.background != null){
            textFieldStyle.background.setLeftWidth(textFieldStyle.background.getLeftWidth() + 30);
        }

        nameField.setStyle(textFieldStyle);
        nameField.getStyle().font.getData().setScale(fontScale);
        nameField.setCursorPosition(0);
        nameField.setAlignment(Align.left);
        nameField.setBlinkTime(0.5f);

        codeField.setStyle(textFieldStyle);
        codeField.getStyle().font.getData().setScale(fontScale);
        codeField.setCursorPosition(0);
        codeField.setAlignment(Align.left);
        codeField.setBlinkTime(0.5f);

        Dialog d = new Dialog(isHost ? "Create Lobby" : "Join Lobby", skin) {
            @Override protected void result(Object object) {
                if (Boolean.TRUE.equals(object)) {
                    if (isHost)    controller.createLobbyWithName(nameField.getText());
                    else           controller.joinLobbyWithName(nameField.getText(), codeField.getText());
                }
            }
        };

        Table content = d.getContentTable();
        content.pad(pad/2);
        content.defaults().width(btnWidth).height(btnHeight/2).pad(pad/4);

        Label nameLabel = new Label("Enter your name:", skin);
        nameLabel.setFontScale(fontScale+1);
        content.add(nameLabel).row();
        content.add(nameField).row();

        if (!isHost) {
            Label codeLabel = new Label("Enter lobby code:", skin);
            codeLabel.setFontScale(fontScale+1);
            content.add(codeLabel).row();
            content.add(codeField).row();
        }
        d.getTitleLabel().setText("");
        d.setBackground(skin.newDrawable("white", new Color(1f, 0.7f, 0.4f, 1f)));
        d.getButtonTable().pad(pad/4);
        d.getButtonTable().defaults().width(400).height(150).pad(pad/2);
        d.button("OK",  true);
        d.button("Cancel", false);

        d.show(stage);

        d.setPosition(
            (screenWidth  - d.getWidth())  / 2,
            screenHeight * 0.36f
        );

        for (Actor actor : d.getButtonTable().getChildren()){
            if (actor instanceof TextButton){
                TextButton button = (TextButton) actor;
                button.getLabel().setFontScale(fontScale);
            }
        }
    }


    public void showError(String msg) {
        Dialog d = new Dialog("Error!", skin) {};

        d.setBackground(skin.newDrawable("white", new Color(1f, 0.7f, 0.4f, 1f)));

        float dialogWidth = screenWidth * 0.6f;
        float dialogHeight = screenHeight * 0.35f;
        d.setSize(dialogWidth, dialogHeight);

        d.getTitleLabel().setFontScale(fontScale * 1.5f);
        d.getTitleTable().padTop(pad*2).padLeft(pad).left();

        Label messageLabel = new Label(msg, skin);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);
        messageLabel.setFontScale(fontScale * 1.1f);

        d.getContentTable().pad(pad/2);
        d.getContentTable().add().height(pad*1.5f).row();
        d.getContentTable().add(messageLabel)
            .width(dialogWidth * 0.8f)
            .padBottom(pad / 2)
            .center()
            .row();

        TextButton okButton = new TextButton("OK", skin);
        okButton.getLabel().setFontScale(fontScale * 1.2f);

        d.getButtonTable().padBottom(pad / 2);
        d.button(okButton, true);

        d.show(stage);

        d.setPosition(
            (screenWidth - dialogWidth) / 2f,
            (screenHeight - dialogHeight) / 2f + pad * 0.25f
        );
    }

    @Override public void resize(int w,int h) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
