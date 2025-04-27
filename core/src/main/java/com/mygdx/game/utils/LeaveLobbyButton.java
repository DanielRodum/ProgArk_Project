package com.mygdx.game.utils;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

/** A pre-styled “Leave Lobby” button that calls your onLeave callback. */
public class LeaveLobbyButton extends TextButton {
    public LeaveLobbyButton(Skin skin, Runnable onLeave) {
        super("Leave Lobby", skin);
        getLabel().setFontScale(3f);             // same size you used in WaitingView
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                onLeave.run();
            }
        });
    }
}
