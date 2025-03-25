package com.mygdx.game;

import java.util.List;

public interface FirebaseInterface {
    interface FirestoreCallback {
        void onSuccess(List<String> words);
        void onFailure(Exception e);
    }

    void fetchWords(FirestoreCallback callback);
}
