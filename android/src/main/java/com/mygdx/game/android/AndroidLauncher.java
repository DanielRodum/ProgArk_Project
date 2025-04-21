package com.mygdx.game.android;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.mygdx.game.FirebaseInterface;
import com.mygdx.game.doodleMain;
import com.mygdx.game.services.PlatformService;

public class AndroidLauncher extends AndroidApplication implements PlatformService {
    private static final int TUTORIAL_REQUEST_CODE = 1001;
    private doodleMain game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase init
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Android config
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;

        // Create FirebaseManager
        FirebaseInterface firebase = new FirebaseManager();

        // Inject PlatformService (this) into doodleMain
        firebase.initializeDatabaseStructure(() -> {
            game = new doodleMain(this);
            game.setFirebaseService(firebase);
            initialize(game, config);
        });
    }

    /** Called from core to launch the in‑app tutorial. */
    @Override
    public void openTutorialVideo() {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivityForResult(intent, TUTORIAL_REQUEST_CODE);
    }

    /** When TutorialActivity finishes, return to main menu. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TUTORIAL_REQUEST_CODE) {
            Gdx.app.postRunnable(() -> game.returnToMainMenu());
        }
    }

    public void setAllowLandscape(boolean allow) {
        if (allow) {
            // Let the sensor decide (portrait or landscape)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        } else {
            // Lock back to portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
