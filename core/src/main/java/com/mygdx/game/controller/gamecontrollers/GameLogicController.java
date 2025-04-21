package com.mygdx.game.controller.gamecontrollers;

import com.mygdx.game.doodleMain;
import com.mygdx.game.model.GameLogicModel;
import com.mygdx.game.model.Player;
import com.mygdx.game.model.WordBank;
import com.mygdx.game.view.gameviews.*;

import java.util.List;

public class GameLogicController {
    private final doodleMain game;
    private final GameLogicModel model;
    private final WordBank wordBank;
    private final Player localPlayer;      // ← new field

    public GameLogicController(
        doodleMain game,
        GameLogicModel model,
        WordBank wordBank,
        Player localPlayer        // ← accept local player here
    ) {
        this.game = game;
        this.model = model;
        this.wordBank = wordBank;
        this.localPlayer = localPlayer;
    }

    /** Start the very first game with the given players. */
    public void startGame(List<Player> players) {
        for (Player p : players) model.addPlayer(p);
        nextRound();
    }

    /** Advance to the next drawer and swap screens. */
    public void nextRound() {
        model.nextDrawer();
        Player drawer = model.getCurrentDrawer();

        if (drawer.equals(localPlayer)) {
            // this client is the drawer
            ChooseWordView choose = new ChooseWordView(game, /* pass controller later */);
            game.setScreen(choose);
            new ChooseWordController(game, choose);
        } else {
            // this client is a guesser
            WaitingForWordView wait = new WaitingForWordView(game, drawer.getName());
            game.setScreen(wait);
            new WaitingForWordController(game, wait, drawer.getName());
        }
    }

    /** Called when the drawer picks a word. */
    public void startRound(String word) {
        model.startRound(word);
        Player drawer = model.getCurrentDrawer();

        if (drawer.equals(localPlayer)) {
            DrawingView draw = new DrawingView(game);
            game.setScreen(draw);
            new DrawingController(game, draw);
        } else {
            GuessingView guess = new GuessingView(game);
            game.setScreen(guess);
            new GuessingController(game, guess, model, localPlayer);
        }
    }

    // ... endRound(), etc ...
}
