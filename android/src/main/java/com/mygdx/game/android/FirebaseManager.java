package com.mygdx.game.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.math.Vector2;
import com.google.firebase.database.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mygdx.game.FirebaseInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FirebaseManager implements FirebaseInterface {
    private final FirebaseDatabase  database  = FirebaseDatabase.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DatabaseReference       lobbyRef;

    @Override
    public void createLobby(String hostName, LobbyCallback callback) {
        String code = generateLobbyCode();
        lobbyRef = database.getReference("lobbies/" + code);

        Map<String,Object> data = new HashMap<>();
        data.put("host",      hostName);
        data.put("status",    "waiting");
        data.put("createdAt", ServerValue.TIMESTAMP);
        data.put("players",   new HashMap<String,Integer>() {{ put(hostName, 0); }});
        data.put("word", "word");

        lobbyRef.setValue(data, (err,ref) -> {
            if (err == null) callback.onSuccess(code);
            else             callback.onFailure(err.getMessage());
        });
    }

    @Override
    public void joinLobby(String lobbyCode, String playerName, LobbyCallback callback) {
        DatabaseReference ref = database.getReference("lobbies/" + lobbyCode);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                if (!snap.exists()) {
                    callback.onFailure("Lobby not found");
                } else if (snap.child("players").hasChild(playerName)) {
                    callback.onFailure("Name already in use");
                } else {
                    snap.getRef()
                        .child("players")
                        .child(playerName)
                        .setValue(true)
                        .addOnSuccessListener(__ -> callback.onSuccess(lobbyCode))
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                }
            }
            @Override public void onCancelled(DatabaseError e) {
                callback.onFailure(e.getMessage());
            }
        });
    }

    private String getCurrentDrawer(String code) {
        try {
            DataSnapshot snap = database
                .getReference("lobbies/" + code + "/currentDrawer")
                .get()
                .getResult();
            return snap.getValue(String.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void startGame(String lobbyCode, String drawerName) {
        DatabaseReference lobby = database.getReference("lobbies/" + lobbyCode);
        // clear previous round data
        lobby.child("strokes").removeValue();
        lobby.child("word").removeValue();
        lobby.child("guessed").removeValue();
        // set new round status
        Map<String,Object> upd = new HashMap<>();
        upd.put("status", "choosing");
        upd.put("currentDrawer", drawerName);
        lobby.updateChildren(upd);
    }

    @Override
    public void recordGuess(String lobbyCode, String playerName) {
        DatabaseReference ref = database.getReference(
            "lobbies/" + lobbyCode + "/guessed/" + playerName);
        ref.setValue(true);
    }

    @Override
    public void subscribeToGuesses(String lobbyCode, GuessesCallback cb) {
        DatabaseReference guessedRef = database.getReference(
            "lobbies/" + lobbyCode + "/guessed");
        DatabaseReference playersRef = database.getReference(
            "lobbies/" + lobbyCode + "/players");

        guessedRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                long guessedCount = snap.getChildrenCount();
                playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot pSnap) {
                        long total = pSnap.getChildrenCount() - 1; // excluding drawer
                        if (guessedCount >= total) {
                            cb.onAllGuessed();
                        }
                    }
                    @Override public void onCancelled(DatabaseError e) {}
                });
            }
            @Override public void onCancelled(DatabaseError e) {}
        });
    }

    @Override
    public void saveChosenWord(String lobbyCode, String word, LobbyCallback callback) {
        DatabaseReference lobby = database.getReference("lobbies/" + lobbyCode);
        // clear any old strokes before new round
        lobby.child("strokes").removeValue();
        Map<String,Object> upd = new HashMap<>();
        upd.put("word", word);
        // Use the actual currentDrawer rather than the host
        String drawer = getCurrentDrawer(lobbyCode);
        upd.put("status", "drawing:" + drawer);
        lobby.updateChildren(upd)
            .addOnSuccessListener(__ -> callback.onSuccess(lobbyCode))
            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    @Override
    public void getChosenWord(String lobbyCode, WordCallback callback) {
        database.getReference("lobbies/" + lobbyCode + "/word")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override public void onDataChange(DataSnapshot snap) {
                    if (snap.exists()) {
                        callback.onSuccess(snap.getValue(String.class));
                    } else {
                        callback.onFailure(new Exception("No word set"));
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError e) {
                    callback.onFailure(e.toException());
                }
            });
    }

    @Override
    public void setupLobbyListener(String lobbyCode, LobbyStateCallback cb) {
        DatabaseReference lobby = database.getReference("lobbies/" + lobbyCode);
        // Listen for status changes only
        lobby.child("status").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                if (!snap.exists()) {
                    cb.onLobbyClosed();
                    return;
                }
                String st = snap.getValue(String.class);
                if (st != null && st.startsWith("drawing:")) {
                    String drawer = st.substring("drawing:".length());
                    cb.onGameStarted(drawer);
                }
            }
            @Override public void onCancelled(DatabaseError e) {
                cb.onLobbyClosed();
            }
        });
        // Listen for word being chosen
        lobby.child("word").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                if (snap.exists()) {
                    String w = snap.getValue(String.class);
                    if (w != null) cb.onWordChosen(w);
                }
            }
            @Override public void onCancelled(DatabaseError e) { }
        });
        // Listen for players joining/leaving
        lobby.child("players").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot d, String p)  { cb.onPlayerJoined(d.getKey()); }
            @Override public void onChildRemoved(DataSnapshot d)          { cb.onPlayerLeft(d.getKey()); }
            @Override public void onChildChanged(DataSnapshot d, String p) {}
            @Override public void onChildMoved(DataSnapshot d, String p)   {}
            @Override public void onCancelled(DatabaseError e)             {}
        });
    }

    @Override
    public void leaveLobby(String lobbyCode, String playerName) {
        DatabaseReference playersRef = database.getReference("lobbies/" + lobbyCode + "/players");
        playersRef.child(playerName).removeValue();

        // if host left ⇒ tear down lobby
        database.getReference("lobbies/" + lobbyCode + "/host")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override public void onDataChange(DataSnapshot snap) {
                    if (playerName.equals(snap.getValue(String.class))) {
                        snap.getRef().getParent().removeValue();
                    }
                }
                @Override public void onCancelled(DatabaseError e) {}
            });
    }

    @Override
    public void fetchWords(FirestoreCallback callback) {
        firestore.collection("WordBank")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<String> out = new ArrayList<>();
                    for (QueryDocumentSnapshot d : task.getResult()) {
                        out.add(d.getString("word"));
                    }
                    callback.onSuccess(out);
                } else {
                    callback.onFailure(task.getException());
                }
            });
    }

    @Override
    public void startDrawingRound(String lobbyCode, String word, String drawer, LobbyCallback callback) {

    }

    @Override
    public void initializeDatabaseStructure(Runnable onComplete) {
        database.getReference("lobbies").keepSynced(true);
        onComplete.run();
    }

    @Override
    public void updatePlayerScore(String lobbyCode, String playerName, int score) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("lobbies").child(lobbyCode).child("players").child(playerName).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer currentScore = currentData.getValue(Integer.class);
                if (currentScore == null) currentScore = 0;
                currentData.setValue(currentScore + score);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        });
    }

    private String generateLobbyCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public void fetchPlayers(String lobbyCode, PlayersCallback cb) {
        DatabaseReference playersRef =
            database.getReference("lobbies/" + lobbyCode + "/players");
        playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                if (!snap.exists()) {
                    cb.onFailure("Lobby disappeared");
                    return;
                }
                List<String> list = new ArrayList<>();
                // Firebase’s Realtime‑DB preserves insertion order of map‐keys,
                // so this will be host first, then joiners in order.
                for (DataSnapshot child : snap.getChildren()) {
                    list.add(child.getKey());
                }
                cb.onSuccess(list);
            }
            @Override public void onCancelled(DatabaseError e) {
                cb.onFailure(e.getMessage());
            }
        });
    }

    @Override
    public void sendStroke(String lobbyCode, String strokeId, List<Vector2> points, String colorHex) {
        DatabaseReference strokeRef =
            database.getReference("lobbies/" + lobbyCode + "/strokes/" + strokeId);

        // pack points as list of maps { x:..., y:... }
        List<Map<String, Double>> pts = new ArrayList<>();
        for (Vector2 v : points) {
            Map<String, Double> m = new HashMap<>();
            m.put("x", (double) v.x);
            m.put("y", (double) v.y);
            pts.add(m);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("points", pts);
        data.put("color",  colorHex);

        strokeRef.setValue(data);
    }

    @Override
    public void subscribeToStrokes(String lobbyCode, StrokeCallback cb) {
        DatabaseReference strokesRef =
            database.getReference("lobbies/" + lobbyCode + "/strokes");

        strokesRef.addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot snap, String prevKey) {
                handleStrokeSnapshot(snap, cb);
            }
            @Override public void onChildChanged(DataSnapshot snap, String prevKey) {
                // existing stroke updated with more points
                handleStrokeSnapshot(snap, cb);
            }
            @Override public void onChildRemoved(DataSnapshot s) {}
            @Override public void onChildMoved(DataSnapshot s, String p)   {}
            @Override public void onCancelled(DatabaseError e)             {}

            private void handleStrokeSnapshot(DataSnapshot snap, StrokeCallback cb) {
                String id   = snap.getKey();
                String hex  = snap.child("color").getValue(String.class);

                List<Vector2> pts = new ArrayList<>();
                for (DataSnapshot pSnap : snap.child("points").getChildren()) {
                    Double x = pSnap.child("x").getValue(Double.class);
                    Double y = pSnap.child("y").getValue(Double.class);
                    if (x != null && y != null) {
                        pts.add(new Vector2(x.floatValue(), y.floatValue()));
                    }
                }
                cb.onStrokeAdded(id, pts, hex);
            }
        });
    }

    @Override
    public void startDrawingRound(String l, String w, LobbyCallback cb) {

    }
}
