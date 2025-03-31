package com.mygdx.game.controller;

import com.mygdx.game.doodleMain;
import com.mygdx.game.view.CreateLobbyView;
import com.mygdx.game.view.MainMenuView;

public class MainMenuController {
    private doodleMain game;
    private MainMenuView view;

    public MainMenuController(doodleMain game, MainMenuView view) {
        this.game = game;
        this.view = view;
    }

    public void handleCreateLobby() {
        System.out.println("Create Lobby Clicked!");
        String lobbyCode = CreateLobbyController.generateLobbyCode();
        game.setScreen(new CreateLobbyView(game, lobbyCode));
    }

    public void handleJoinLobby() {
        System.out.println("Join Lobby Clicked!");
        // TODO: implement logic
    }

    public void handleTutorial() {
        System.out.println("Tutorial Clicked!");
        // TODO: implement logic
    }
}
