package com.mygdx.game.model;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    private List<Player> players;
    private Player currentDrawer;
    private String currentWord;
    private GameState gameState;
    private int currentRound;
    private int maxRounds;
    private long roundStartTime;

    public enum GameState {
        WAITING_FOR_PLAYERS,
        CHOOSING_WORD,
        DRAWING,
        ROUND_END,
        GAME_END
    }

    public GameLogic() {
        this.players = new ArrayList<>();
        this.gameState = GameState.WAITING_FOR_PLAYERS;
        this.currentRound = 0;
    }

    public void addPlayer(Player player) {
        players.add(player);
        if (players.size() == 1) {
            currentDrawer = player;
        }
        this.maxRounds = players.size();
    }

    public void setCurrentWord(String word) {
        if (gameState == GameState.CHOOSING_WORD) {
            this.currentWord = word;
            gameState = GameState.DRAWING;
            roundStartTime = System.currentTimeMillis();
        }
    }

    public void startGame() {
        if (players.size() >= 2) {
            gameState = GameState.CHOOSING_WORD;
            currentRound = 1;
        }
    }

    public void nextTurn() {
        int currentIndex = players.indexOf(currentDrawer);
        int nextIndex = (currentIndex + 1) % players.size();

        currentDrawer = players.get(nextIndex);

        if (nextIndex == 0) {
            currentRound++;
            if (currentRound > maxRounds) {
                gameState = GameState.GAME_END;
                return;
            }
        }

        gameState = GameState.CHOOSING_WORD;
        currentWord = null;
    }

    public boolean guessWord(Player player, String guess) {
        if (gameState == GameState.DRAWING && player != currentDrawer) {
            boolean correct = guess.equalsIgnoreCase(currentWord);
            if (correct) {
                long timeElapsed = (System.currentTimeMillis() - roundStartTime) / 1000;
                int score = Math.max(0, 100 - (int) timeElapsed);
                player.addScore(score);
            }
            return correct;
        }
        return false;
    }

    public Player getCurrentDrawer() {
        return currentDrawer;
    }

    public GameState getGameState() {
        return gameState;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public List<Player> getPlayers() {
        return players;
    }
}