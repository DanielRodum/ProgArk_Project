package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.view.MainMenuView;

/**
 * Entry point for the game. On AndroidLauncher we set a real FirebaseManager;
 * on desktop (or if none set) we use a no‑op stub to avoid null pointers.
 */
public class doodleMain extends Game {
    private SpriteBatch batch;
    private String playerName;
    private FirebaseInterface firebaseService;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // If no FirebaseManager was injected, use a stub:
        if (firebaseService == null) {
            firebaseService = new NullFirebaseManager();
        }
        setScreen(new MainMenuView(this));
    }

    public SpriteBatch getBatch() { return batch; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String name) { this.playerName = name; }
    public FirebaseInterface getFirebaseService() { return firebaseService; }
    public void setFirebaseService(FirebaseInterface service) {
        this.firebaseService = service;
    }

    @Override
    public void dispose() {
        batch.dispose();
        // Safe leave logic (stub does nothing)
        firebaseService.leaveLobby("any", playerName);
        super.dispose();
    }

    /** A simple no‑op stub so desktop runs without Firebase. */
    private static class NullFirebaseManager implements FirebaseInterface {
        @Override public void fetchWords(FirestoreCallback cb)               { cb.onSuccess(java.util.List.of("Cat","Dog","House")); }
        @Override public void startDrawingRound(String l, String w, LobbyCallback cb) { cb.onSuccess(l); }
        @Override public void createLobby(String h, LobbyCallback cb)        { cb.onFailure("Offline"); }
        @Override public void joinLobby(String c, String p, LobbyCallback cb){ cb.onFailure("Offline"); }
        @Override public void startGame(String l, String d)                  {}
        @Override public void leaveLobby(String l, String p)                 {}
        @Override public void setupLobbyListener(String l, LobbyStateCallback c){ }
        @Override public void initializeDatabaseStructure(Runnable rc)       { rc.run(); }
    }
}
