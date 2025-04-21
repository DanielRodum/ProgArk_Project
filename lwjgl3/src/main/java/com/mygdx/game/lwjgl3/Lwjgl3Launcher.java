package com.mygdx.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.doodleMain;
import com.mygdx.game.services.PlatformService;

/** Desktop launcher with a stub tutorial implementation. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        // Provide a no‑op PlatformService for desktop
        PlatformService stub = new PlatformService() {
            @Override
            public void openTutorialVideo() {
                // no tutorial on desktop for now
                System.out.println("Tutorial requested (desktop stub)");
            }

            @Override
            public void setAllowLandscape(boolean allow) {

            }
        };
        return new Lwjgl3Application(
            new doodleMain(stub),
            getDefaultConfiguration()
        );
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("DoodleGame");
        config.setWindowedMode(800, 600);
        return config;
    }
}
