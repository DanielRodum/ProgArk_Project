package com.mygdx.game.view.gameviews;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class GuessingView extends Table {
    public GuessingView(Skin skin, String word) {
        setFillParent(true);
        center();

        // Display the masked word
        String maskedWord = word.replaceAll("[a-zA-Z]", "_ ");
        Label maskedWordLabel = new Label("Guess the word: " + maskedWord, skin);
        add(maskedWordLabel).padBottom(20f).row();

        // Text field for the guess input
        TextField guessField = new TextField("", skin);
        guessField.setMessageText("Enter your guess...");
        add(guessField).width(300f).padBottom(20f).row();

        // Button for submitting guess (just a placeholder here for now)
        TextButton submitButton = new TextButton("Submit Guess", skin);
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String guess = guessField.getText();
                System.out.println("Guess: " + guess); // Placeholder for submitting guess logic
            }
        });
        add(submitButton).pad(10f).row();
    }
}
