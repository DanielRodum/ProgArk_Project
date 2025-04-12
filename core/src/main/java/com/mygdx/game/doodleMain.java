package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.view.MainMenuView;

public class doodleMain extends Game {
    private SpriteBatch batch;
    private String playerName;
    private FirebaseInterface firebaseService;

    @Override public void create() {
        batch = new SpriteBatch();
        setScreen(new MainMenuView(this));
    }

    // Getters/Setters
    public SpriteBatch getBatch() { return batch; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String name) { this.playerName = name; }
    public void setFirebaseService(FirebaseInterface service) { this.firebaseService = service; }
    public FirebaseInterface getFirebaseService() { return firebaseService; }

    @Override public void dispose() {
        batch.dispose();
        if (firebaseService != null) firebaseService.leaveLobby("any_active_lobby", playerName);
    }
}
