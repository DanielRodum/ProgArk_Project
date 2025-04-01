package com.mygdx.game.android;

import com.google.firebase.database.*;
import com.google.firebase.firestore.*;
import com.mygdx.game.FirebaseInterface;
import java.util.*;

public class FirebaseManager implements FirebaseInterface {
    private final FirebaseDatabase database;
    private final FirebaseFirestore firestore;

    public FirebaseManager() {
        database = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void fetchWords(FirestoreCallback callback) {
        firestore.collection("WordBank")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<String> words = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        words.add(doc.getString("word"));
                    }
                    callback.onSuccess(words);
                } else {
                    callback.onFailure(task.getException());
                }
            });
    }
}
