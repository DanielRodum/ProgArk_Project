package com.mygdx.game.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.firebase.FirebaseApp;
import com.mygdx.game.doodleMain;
import com.mygdx.game.FirebaseInterface;

public class AndroidLauncher extends AndroidApplication {
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
}
