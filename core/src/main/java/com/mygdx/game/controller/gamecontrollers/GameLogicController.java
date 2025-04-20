package com.mygdx.game.controller.gamecontrollers;

import com.mygdx.game.model.GameStateModel;
import com.mygdx.game.model.Player;

public class GameLogicController {
    private GameStateModel gameState;
    private List<String> wordBank;

    public GameLogicController(GameStateModel gameState, List<String> wordBank) {
        this.gameState = gameState;
        this.wordBank = wordBank;
    }

    public void startGame() {
        startNewRound();
    }

    public void startNewRound() {
        String randomWord = wordBank.getRandomWord();
        gameState.startNewRound(randomWord);
        System.out.println("New round started! Drawer: " + gameState.getCurrentDrawer().getName());
    }

    public void submitGuess(Player player, String guess) {
        boolean isCorrect = guess.equalsIgnoreCase(gameState.getCurrentWord());
        gameState.addGuess(player, guess, isCorrect);
        player.updateStats(isCorrect);

        if (isCorrect) {
            System.out.println(player.getName() + " guessed correctly! Points awarded.");
        } else {
            System.out.println(player.getName() + " guessed: " + guess);
        }
    }
}
