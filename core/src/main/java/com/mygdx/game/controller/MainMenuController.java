package com.mygdx.game.controller;

import com.mygdx.game.DoodleWars;
import com.mygdx.game.view.MainMenuView;

public class MainMenuController {
    private DoodleWars game;
    private MainMenuView view;

    public MainMenuController(DoodleWars game, MainMenuView view){
        this.game = game;
        this.view = view;
    }

    public void handleCreateLobby(){
        System.out.println("Create Lobby Clicked!");
        //TODO: implement logic
    }

    public void handleJoinLobby(){
        System.out.println("Join Lobby Clicked!");
        //TODO: implement logic
    }

    public void handleTutorial(){
        System.out.println("Tutorial Clicked!");
        //TODO: implement logic
    }
}
