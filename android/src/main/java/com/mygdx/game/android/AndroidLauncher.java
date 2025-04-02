package com.mygdx.game.android;

import android.content.Intent;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.FirebaseApp;
import com.mygdx.game.doodleMain;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication implements doodleMain.AndroidBridge{
    private doodleMain game;
    private static final int REQUEST_TUTORIAL = 1;

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
    public void openTutorial(){
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TUTORIAL){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new com.mygdx.game.view.MainMenuView(game));
                }
            });
        }
    }
}
