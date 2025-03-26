package com.mygdx.game.controller;

import com.mygdx.game.doodleMain;
import com.mygdx.game.view.CreateLobbyView;

import java.util.Random;

public class CreateLobbyController {
    private doodleMain game;
    private CreateLobbyView view;

    public CreateLobbyController(doodleMain game, CreateLobbyView view) {
        this.game = game;
        this.view = view;
    }

    /**
     * Generate a random lobby code
     *
     * @return A random alphanumeric code
     */
    public static String generateLobbyCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        return code.toString();
    }

    /**
     * Handle player joining the lobby
     *
     * @param playerName Name of the player joining
     */
    public void handlePlayerJoin(String playerName) {
        view.addPlayer(playerName);
        System.out.println("Player joined: " + playerName);
    }

    /**
     * Handle player leaving the lobby
     *
     * @param playerName Name of the player leaving
     */
    public void handlePlayerLeave(String playerName) {
        view.removePlayer(playerName);
        System.out.println("Player left: " + playerName);
    }

    /**
     * Handle starting the game
     */
    public void handleStartGame() {
        System.out.println("Game starting!");
        // TODO: Implement game start logic
    }
}
