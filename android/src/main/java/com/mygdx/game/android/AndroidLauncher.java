package com.mygdx.game.android;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.FirebaseApp;
import com.mygdx.game.doodleMain;
import com.mygdx.game.services.PlatformService;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication implements PlatformService {
    private doodleMain game;
    private static final int TUTORIAL_REQUEST_CODE = 1001;
    private static final int REQUEST_TUTORIAL = 1;
    private boolean returnToMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.

        FirebaseApp.initializeApp(this);
        FirebaseManager firebaseManager = new FirebaseManager();

        game = new doodleMain(this);
        game.setFirebaseService(firebaseManager); // Connect Firebase

        initialize(game, configuration);
    }

    @Override
    public void openTutorialVideo() {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivityForResult(intent, TUTORIAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TUTORIAL_REQUEST_CODE){
            Gdx.app.postRunnable(()->{
                game.returnToMainMenu();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
