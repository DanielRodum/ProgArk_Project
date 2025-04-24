package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.services.PlatformService;
import com.mygdx.game.view.MainMenuView;

import java.util.Collections;
import java.util.List;

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
                @Override
                public void fetchWords(FirestoreCallback cb) {
                    cb.onSuccess(Collections.<String>emptyList());
                }

                @Override
                public void startDrawingRound(String lobbyCode, String word, String drawer, LobbyCallback callback) {
                    callback.onSuccess(lobbyCode);
                }

                @Override
                public void startDrawingRound(String l, String w, LobbyCallback cb) {
                    cb.onSuccess(l);
                }

                @Override
                public void saveChosenWord(String lobbyCode, String word, LobbyCallback callback) {
                    // no‐op
                }

                @Override
                public void getChosenWord(String lobbyCode, WordCallback callback) {
                    callback.onSuccess("");
                }

                @Override
                public void createLobby(String h, LobbyCallback cb) {
                    cb.onFailure("Offline");
                }

                @Override
                public void joinLobby(String c, String p, LobbyCallback cb) {
                    cb.onFailure("Offline");
                }

                @Override
                public void startGame(String l, String d) {
                    // no‐op
                }

                @Override
                public void leaveLobby(String l, String p) {
                    // no‐op
                }

                @Override
                public void setupLobbyListener(String l, LobbyStateCallback c) {
                    // no‐op
                }

                @Override
                public void initializeDatabaseStructure(Runnable onComplete) {
                    onComplete.run();
                }

                @Override
                public void updatePlayerScore(String lobbyCode, String playerName, int score) {

                }

                @Override
                public void fetchPlayers(String lobbyCode, PlayersCallback cb) {
                    cb.onSuccess(Collections.<String>emptyList());
                }

                @Override
                public void sendStroke(String lobbyCode,
                                       String strokeId,
                                       List<Vector2> points,
                                       String colorHex) {
                    // no‐op
                }

                @Override
                public void subscribeToStrokes(String lobbyCode, StrokeCallback cb) {
                    // no‐op
                }

                @Override
                public void recordGuess(String lobbyCode, String playerName) {

                }

                @Override
                public void subscribeToGuesses(String lobbyCode, GuessesCallback callback) {

                }
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
}
