// FirebaseManager.java
package com.mygdx.game.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.math.Vector2;
import com.google.firebase.database.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mygdx.game.FirebaseInterface;

import java.util.*;

public class FirebaseManager implements FirebaseInterface {
    private final FirebaseDatabase database  = FirebaseDatabase.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public void createLobby(String hostName, LobbyCallback callback) {
        String code = generateLobbyCode();
        DatabaseReference lobbyRef = database.getReference("lobbies/" + code);
        Map<String,Object> data = new HashMap<>();
        data.put("host",      hostName);
        data.put("status",    "waiting");
        data.put("createdAt", ServerValue.TIMESTAMP);
        Map<String,Integer> players = new HashMap<>();
        players.put(hostName, 0);
        data.put("players", players);
        // no default word here
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
                        .setValue(0)  // start with zero points
                        .addOnSuccessListener(__ -> callback.onSuccess(lobbyCode))
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                }
            }
            @Override public void onCancelled(DatabaseError e) {
                callback.onFailure(e.getMessage());
            }
        });
    }

    @Override
    public void fetchPlayers(String lobbyCode, PlayersCallback cb) {
        database.getReference("lobbies/" + lobbyCode + "/players")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override public void onDataChange(DataSnapshot snap) {
                    if (!snap.exists()) {
                        cb.onFailure("Lobby disappeared");
                        return;
                    }
                    List<String> out = new ArrayList<>();
                    for (DataSnapshot c : snap.getChildren()) {
                        out.add(c.getKey());
                    }
                    cb.onSuccess(out);
                }
                @Override public void onCancelled(DatabaseError e) {
                    cb.onFailure(e.getMessage());
                }
            });
    }

    @Override
    public void leaveLobby(String lobbyCode, String playerName) {
        // remove player, tear down lobby if host leaves
        DatabaseReference playersRef = database.getReference("lobbies/" + lobbyCode + "/players");
        playersRef.child(playerName).removeValue();
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
    public void setupLobbyListener(String lobbyCode, LobbyStateCallback cb) {
        DatabaseReference lobby = database.getReference("lobbies/" + lobbyCode);

        // 1) Listen for status flips: "choosing" → drawer picks word
        lobby.child("status").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                if (!snap.exists()) {
                    cb.onLobbyClosed();
                    return;
                }
                String st = snap.getValue(String.class);
                if ("choosing".equals(st)) {
                    // pull who’s set as the next drawer
                    database.getReference("lobbies/" + lobbyCode + "/currentDrawer")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override public void onDataChange(DataSnapshot ds) {
                                String drawer = ds.getValue(String.class);
                                if (drawer != null) cb.onGameStarted(drawer);
                            }
                            @Override public void onCancelled(DatabaseError e) {}
                        });
                } else if (st != null && st.startsWith("drawing:")) {
                    // word has been chosen, drawing underway
                    String drawer = st.substring("drawing:".length());
                    cb.onGameStarted(drawer);
                }
            }
            @Override public void onCancelled(DatabaseError e) {
                cb.onLobbyClosed();
            }
        });

        // 2) Listen for the word having been chosen
        lobby.child("word").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                if (snap.exists()) {
                    String w = snap.getValue(String.class);
                    if (w != null) cb.onWordChosen(w);
                }
            }
            @Override public void onCancelled(DatabaseError e) {}
        });

        // 3) Listen for players joining / leaving
        lobby.child("players").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot d, String p)  { cb.onPlayerJoined(d.getKey()); }
            @Override public void onChildRemoved(DataSnapshot d)          { cb.onPlayerLeft(d.getKey()); }
            @Override public void onChildChanged(DataSnapshot d, String p) {}
            @Override public void onChildMoved(DataSnapshot d, String p)   {}
            @Override public void onCancelled(DatabaseError e)             {}
        });
    }

    @Override
    public void initializeDatabaseStructure(Runnable onComplete) {

    }

    @Override public void startGame(String lobbyCode, String drawerName) {
        DatabaseReference lobby = database.getReference("lobbies/" + lobbyCode);
        lobby.child("strokes").removeValue();
        lobby.child("word").removeValue();
        lobby.child("guessed").removeValue();
        Map<String,Object> upd = new HashMap<>();
        upd.put("status",        "choosing");
        upd.put("currentDrawer", drawerName);
        lobby.updateChildren(upd);
    }

    @Override
    public void fetchWords(FirestoreCallback cb) {
        firestore.collection("WordBank")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<String> out = new ArrayList<>();
                    for (QueryDocumentSnapshot d : task.getResult()) {
                        out.add(d.getString("word"));
                    }
                    cb.onSuccess(out);
                } else {
                    cb.onFailure(task.getException());
                }
            });
    }

    @Override
    public void startDrawingRound(String lobbyCode, String word, String drawer, LobbyCallback callback) {

    }

    @Override public void saveChosenWord(String lobbyCode, String word, LobbyCallback cb) {
        DatabaseReference lobby = database.getReference("lobbies/" + lobbyCode);
        lobby.child("strokes").removeValue();
        Map<String,Object> upd = new HashMap<>();
        upd.put("word", word);
        upd.put("status", "drawing:" + getCurrentDrawer(lobbyCode));
        lobby.updateChildren(upd)
            .addOnSuccessListener(__ -> cb.onSuccess(lobbyCode))
            .addOnFailureListener(e  -> cb.onFailure(e.getMessage()));
    }

    @Override public void getChosenWord(String lobbyCode, WordCallback cb) {
        database.getReference("lobbies/" + lobbyCode + "/word")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override public void onDataChange(DataSnapshot snap) {
                    if (snap.exists()) cb.onSuccess(snap.getValue(String.class));
                    else               cb.onFailure(new Exception("No word set"));
                }
                @Override public void onCancelled(@NonNull DatabaseError e) {
                    cb.onFailure(e.toException());
                }
            });
    }

    @Override public void sendStroke(String lobbyCode, String strokeId, List<Vector2> pts, String colorHex) {
        DatabaseReference ref = database.getReference("lobbies/" + lobbyCode + "/strokes/" + strokeId);
        List<Map<String,Double>> packed = new ArrayList<>();
        for (Vector2 v : pts) {
            Map<String,Double> m = new HashMap<>();
            m.put("x", (double)v.x);
            m.put("y", (double)v.y);
            packed.add(m);
        }
        Map<String,Object> data = new HashMap<>();
        data.put("points", packed);
        data.put("color",  colorHex);
        ref.setValue(data);
    }

    @Override public void subscribeToStrokes(String lobbyCode, StrokeCallback cb) {
        DatabaseReference strokesRef = database.getReference("lobbies/" + lobbyCode + "/strokes");
        strokesRef.addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot snap, String pk)   { handleStroke(snap, cb); }
            @Override public void onChildChanged(DataSnapshot snap, String pk) { handleStroke(snap, cb); }
            @Override public void onChildRemoved(DataSnapshot s)               {}
            @Override public void onChildMoved(DataSnapshot s, String p)       {}
            @Override public void onCancelled(DatabaseError e)                 {}

            private void handleStroke(DataSnapshot snap, StrokeCallback cb) {
                String id  = snap.getKey();
                String hex = snap.child("color").getValue(String.class);
                List<Vector2> pts = new ArrayList<>();
                for (DataSnapshot ps : snap.child("points").getChildren()) {
                    Double x = ps.child("x").getValue(Double.class);
                    Double y = ps.child("y").getValue(Double.class);
                    if (x!=null && y!=null) pts.add(new Vector2(x.floatValue(), y.floatValue()));
                }
                cb.onStrokeAdded(id, pts, hex);
            }
        });
    }

    @Override public void recordGuess(String lobbyCode, String playerName) {
        database.getReference("lobbies/" + lobbyCode + "/guessed/" + playerName)
            .setValue(true);
    }

    @Override public void subscribeToGuesses(String lobbyCode, GuessesCallback cb) {
        DatabaseReference guessedRef  = database.getReference("lobbies/" + lobbyCode + "/guessed");
        DatabaseReference playersRef  = database.getReference("lobbies/" + lobbyCode + "/players");
        guessedRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                long guessed = snap.getChildrenCount();
                playersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot ps) {
                        long total = ps.getChildrenCount() - 1;
                        if (guessed >= total) cb.onAllGuessed();
                    }
                    @Override public void onCancelled(DatabaseError e) {}
                });
            }
            @Override public void onCancelled(DatabaseError e) {}
        });
    }

    @Override
    public void startDrawingRound(String l, String w, LobbyCallback cb) {

    }

    @Override public void updatePlayerScore(String lobbyCode, String playerName, int delta) {
        DatabaseReference ref = database.getReference("lobbies/" + lobbyCode + "/players/" + playerName);
        ref.runTransaction(new Transaction.Handler() {
            @NonNull @Override public Transaction.Result doTransaction(@NonNull MutableData m) {
                Integer cur = m.getValue(Integer.class);
                if (cur == null) cur = 0;
                m.setValue(cur + delta);
                return Transaction.success(m);
            }
            @Override public void onComplete(@Nullable DatabaseError e, boolean c, @Nullable DataSnapshot s) {}
        });
    }

    // helper to read currentDrawer
    private String getCurrentDrawer(String code) {
        try {
            DataSnapshot snap = database
                .getReference("lobbies/" + code + "/currentDrawer")
                .get().getResult();
            return snap.getValue(String.class);
        } catch (Exception x) {
            return null;
        }
    }

    private String generateLobbyCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}
