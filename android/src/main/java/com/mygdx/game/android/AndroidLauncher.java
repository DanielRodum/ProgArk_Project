package com.mygdx.game.android;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.FirebaseApp;
import com.mygdx.game.doodleMain;
import com.mygdx.game.services.PlatformService;
import com.mygdx.game.FirebaseInterface;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication implements PlatformService {
    private doodleMain game;
    private static final int TUTORIAL_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();

        FirebaseApp.initializeApp(this);
        FirebaseInterface firebase = new FirebaseManager();

        // Initialize database structure
        firebase.initializeDatabaseStructure(() -> {
            doodleMain game = new doodleMain();
            game.setFirebaseService(firebase);
            initialize(game, configuration);
        });
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
