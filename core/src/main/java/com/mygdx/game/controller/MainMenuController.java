package com.mygdx.game.controller;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.view.CreateLobbyView;
import com.mygdx.game.view.MainMenuView;
import com.mygdx.game.view.JoinLobbyView;

public class MainMenuController {
    private final doodleMain game;
    private final MainMenuView view;

    public MainMenuController(doodleMain game, MainMenuView view) {
        this.game = game;
        this.view = view;
    }

    public void handleCreateLobby() {
        view.showNameInputDialog(true); // true = is host
    }

    public void handleJoinLobby() {
        view.showJoinDialog();
    }

    public void handleTutorial() {
        // Placeholder for tutorial flow
        Gdx.app.log("Tutorial", "Launch tutorial view here");
    }

    public void createLobbyWithName(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            view.showError("Please enter a valid name");
            return;
        }

        game.setPlayerName(playerName);
        String lobbyCode = generateLobbyCode();
        game.setScreen(new CreateLobbyView(game, lobbyCode));
    }

    public void joinLobbyWithName(String playerName, String lobbyCode) {
        game.setPlayerName(playerName);
        game.getFirebaseService().joinLobby(lobbyCode, playerName,
            new FirebaseInterface.LobbyCallback() {
                @Override
                public void onSuccess(String code) {
                    Gdx.app.postRunnable(() ->
                        game.setScreen(new JoinLobbyView(game, code))
                    );
                }

                @Override
                public void onFailure(String error) {
                    Gdx.app.postRunnable(() ->
                        view.showError("Join failed: " + error)
                    );
                }
            });
    }

    private String generateLobbyCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }
}
