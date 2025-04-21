package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.services.PlatformService;
import com.mygdx.game.view.MainMenuView;

/** Core game class. */
public class doodleMain extends Game {
    private SpriteBatch batch;
    private String playerName;
    private FirebaseInterface firebaseService;
    private final PlatformService platformService;

    /** No‑arg ctor for desktop: platformService will be null. */
    public doodleMain() {
        this.platformService = null;
    }

    /** AndroidLauncher will call this with itself. */
    public doodleMain(PlatformService platformService) {
        this.platformService = platformService;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        if (firebaseService == null) {
            // stub to avoid NPE on desktop
            firebaseService = new FirebaseInterface() {
                @Override public void fetchWords(FirestoreCallback cb)     { cb.onSuccess(java.util.List.of()); }
                @Override public void startDrawingRound(String l, String w, LobbyCallback cb) { cb.onSuccess(l); }

                @Override
                public void getChosenWord(String lobbyCode, WordCallback callback) {
                    callback.onSuccess(lobbyCode);
                }

                @Override public void createLobby(String h, LobbyCallback cb) { cb.onFailure("Offline"); }
                @Override public void joinLobby(String c, String p, LobbyCallback cb) { cb.onFailure("Offline"); }
                @Override public void startGame(String l, String d)        {}
                @Override public void leaveLobby(String l, String p)       {}
                @Override public void setupLobbyListener(String l, LobbyStateCallback c) {}
                @Override public void initializeDatabaseStructure(Runnable r){ r.run(); }
            };
        }
        setScreen(new MainMenuView(this));
    }

    public SpriteBatch getBatch() { return batch; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String name) { this.playerName = name; }
    public FirebaseInterface getFirebaseService() { return firebaseService; }
    public void setFirebaseService(FirebaseInterface s) { this.firebaseService = s; }

    /** Core call to open tutorial on the current platform. */
    public void openTutorial() {
        if (platformService != null) {
            platformService.openTutorialVideo();
        }
    }

    /** Called by AndroidLauncher after TutorialActivity finishes. */
    public void returnToMainMenu() {
        setScreen(new MainMenuView(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        firebaseService.leaveLobby("any", playerName);
        super.dispose();
    }

    /** A simple no‑op stub so desktop runs without Firebase. */
    private static class NullFirebaseManager implements FirebaseInterface {
        @Override public void fetchWords(FirestoreCallback cb) {
            cb.onSuccess(java.util.List.of("Cat", "Dog", "House"));
        }

        @Override public void startDrawingRound(String l, String w, LobbyCallback cb) {
            cb.onSuccess(l);
        }

        @Override public void createLobby(String h, LobbyCallback cb) {
            cb.onFailure("Offline");
        }

        @Override public void joinLobby(String c, String p, LobbyCallback cb) {
            cb.onFailure("Offline");
        }

        @Override public void startGame(String l, String d) {}

        @Override public void leaveLobby(String l, String p) {}

        @Override public void setupLobbyListener(String l, LobbyStateCallback c) {}

        @Override public void initializeDatabaseStructure(Runnable rc) {
            rc.run();
        }

        @Override
        public void saveChosenWord(String lobbyCode, String word, LobbyCallback callback) {
            callback.onFailure("Offline");
        }

        @Override
        public void getChosenWord(String lobbyCode, WordCallback callback) {
            callback.onFailure(new Exception("Offline mode — no word available"));
        }
    }

}
