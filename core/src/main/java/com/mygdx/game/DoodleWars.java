package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.view.MainMenuView;

public class DoodleWars extends Game {
    @Override
    public void create(){
        setScreen(new MainMenuView(this));
    }
}
