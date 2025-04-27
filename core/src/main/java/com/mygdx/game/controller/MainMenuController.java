package com.mygdx.game.controller;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.view.MainMenuView;
import com.mygdx.game.view.TutorialView;
import com.mygdx.game.view.WaitingView;
import com.mygdx.game.view.gameviews.ChooseWordView;
import com.mygdx.game.view.gameviews.DrawingView;

import java.util.Locale;

public class MainMenuController {
    private final doodleMain game;
    private final MainMenuView view;

    public MainMenuController(doodleMain game, MainMenuView view) {
        this.game = game;
        this.view = view;
    }

    public void handleCreateLobby() {
        view.showNameInputDialog(true);
    }

    public void handleJoinLobby() {
        view.showNameInputDialog(false);
    }

    /** Switches to the libGDX TutorialView, which immediately calls openTutorial(). */
    public void handleTutorial() {
        game.setScreen(new TutorialView(game));
    }

    public void createLobbyWithName(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            view.showError("Please enter a valid name");
            Gdx.input.setOnscreenKeyboardVisible(false); //remove keyboard
            return;
        }
        game.setPlayerName(playerName);
        game.getFirebaseService().createLobby(playerName, new FirebaseInterface.LobbyCallback() {
            @Override public void onSuccess(String code) {
                Gdx.app.postRunnable(() ->
                    game.setScreen(new WaitingView(game, code, true))
                );
                Gdx.input.setOnscreenKeyboardVisible(false); //remove keyboard when OK
            }
            @Override public void onFailure(String error) {
                Gdx.app.postRunnable(() ->
                    view.showError("Create failed: " + error)
                );
                Gdx.input.setOnscreenKeyboardVisible(false); //remove keyboard
            }
        });
    }

    public void joinLobbyWithName(String playerName, String lobbyCode) {
        if (playerName == null || playerName.trim().isEmpty()) {
            view.showError("Please enter a valid name");
            Gdx.input.setOnscreenKeyboardVisible(false); //remove keyboard
            return;
        }
        if (lobbyCode == null || lobbyCode.trim().isEmpty()) {
            view.showError("Please enter a valid lobby code");
            Gdx.input.setOnscreenKeyboardVisible(false); //remove keyboard
            return;
        }
        String code = lobbyCode.trim().toUpperCase(Locale.US);

        game.setPlayerName(playerName);
        game.getFirebaseService().joinLobby(code, playerName, new FirebaseInterface.LobbyCallback() {
            @Override
            public void onSuccess(String code) {
                Gdx.app.postRunnable(() ->
                    game.setScreen(new WaitingView(game, code, false))
                );
                Gdx.input.setOnscreenKeyboardVisible(false); //remove keyboard when OK
            }
            @Override
            public void onFailure(String error) {
                Gdx.app.postRunnable(() ->
                    view.showError("Join failed: " + error)
                );
                Gdx.input.setOnscreenKeyboardVisible(false); //remove keyboard
            }
        });
    }
}
