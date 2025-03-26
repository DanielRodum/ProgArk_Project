package com.mygdx.game.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.game.DoodleWars;
import com.google.firebase.FirebaseApp;
import com.mygdx.game.doodleMain;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.

        FirebaseApp.initializeApp(this);
        FirebaseManager firebaseManager = new FirebaseManager();

        doodleMain game = new doodleMain();
        game.setFirebaseService(firebaseManager); // Connect Firebase

        initialize(game, configuration);
    }
}
