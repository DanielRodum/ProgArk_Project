package com.mygdx.game.android;

import androidx.annotation.NonNull;

import com.google.firebase.database.*;
import com.mygdx.game.FirebaseInterface;
import java.util.HashMap;
import java.util.Map;

public class FirebaseManager implements FirebaseInterface {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference lobbyRef;

    @Override
    public void createLobby(String hostName, LobbyCallback callback) {
        String code = generateLobbyCode();
        lobbyRef = database.getReference("lobbies/" + code);
        Map<String,Object> data = new HashMap<>();
        data.put("host", hostName);
        data.put("status","waiting");
        data.put("createdAt",ServerValue.TIMESTAMP);
        data.put("players", new HashMap<String,Boolean>(){{ put(hostName,true); }});
        lobbyRef.setValue(data, (e,r)-> {
            if (e==null) callback.onSuccess(code);
            else         callback.onFailure(e.getMessage());
        });
    }

    @Override
    public void joinLobby(String lobbyCode, String playerName, LobbyCallback callback) {
        DatabaseReference players = database.getReference("lobbies/" + lobbyCode + "/players");
        players.getParent().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot s) {
                if (!s.exists()) { callback.onFailure("Lobby not found"); return; }
                if (s.child("players").hasChild(playerName)) {
                    callback.onFailure("Name already in use");
                    return;
                }
                players.child(playerName).setValue(true)
                    .addOnSuccessListener(__ -> callback.onSuccess(lobbyCode))
                    .addOnFailureListener(ex -> callback.onFailure(ex.getMessage()));
            }
            @Override public void onCancelled(DatabaseError e) { callback.onFailure(e.getMessage()); }
        });
    }

    @Override public void startGame(String lobbyCode, String drawerName) {
        database.getReference("lobbies/" + lobbyCode)
            .child("status").setValue("drawing:" + drawerName);

        database.getReference("lobbies/"+lobbyCode+"/word").setValue("word");
    }

    @Override public void leaveLobby(String lobbyCode, String playerName) {
        DatabaseReference ref = database.getReference("lobbies/" + lobbyCode);
        ref.child("players").child(playerName).removeValue();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot s) {
                if (playerName.equals(s.child("host").getValue(String.class))) {
                    ref.removeValue();
                }
            }
            @Override public void onCancelled(DatabaseError e) {}
        });
    }

    @Override
    public void setupLobbyListener(String lobbyCode, LobbyStateCallback cb) {
        lobbyRef = database.getReference("lobbies/" + lobbyCode);
        lobbyRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot s) {
                if (!s.exists()) { cb.onLobbyClosed(); return; }
                String st = s.child("status").getValue(String.class);
                if (st!=null && st.startsWith("drawing:")) {
                    cb.onGameStarted(st.split(":")[1]);
                }
            }
            @Override public void onCancelled(DatabaseError e) { cb.onLobbyClosed(); }
        });
        lobbyRef.child("players").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot d, String p)    { cb.onPlayerJoined(d.getKey()); }
            @Override public void onChildRemoved(DataSnapshot d)            { cb.onPlayerLeft(d.getKey()); }
            @Override public void onChildChanged(DataSnapshot d, String p)  {}
            @Override public void onChildMoved(DataSnapshot d, String p)    {}
            @Override public void onCancelled(DatabaseError e)              {}
        });
    }

    @Override public void initializeDatabaseStructure(Runnable onComplete) {
        database.getReference("lobbies").keepSynced(true);
        onComplete.run();
    }

    // Word‐round stubs (compiler happy; flesh out later)
    @Override public void fetchWords(FirestoreCallback cb) { cb.onSuccess(java.util.List.of("Cat","Dog")); }
    @Override public void startDrawingRound(String lobbyCode, String word, String drawer, LobbyCallback cb) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "drawer:"+drawer);
        updates.put("word", word);

        database.getReference("lobbies/"+lobbyCode).updateChildren(updates).addOnSuccessListener(__ -> cb.onSuccess(lobbyCode)).addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    @Override
    public void startDrawingRound(String l, String w, LobbyCallback cb) {

    }

    @Override
    public void saveChosenWord(String lobbyCode, String word, LobbyCallback callback) {
        DatabaseReference wordRef = database.getReference("lobbies/"+lobbyCode+"/word");
        wordRef.setValue(word).addOnSuccessListener(unused -> callback.onSuccess("Word saved")).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    @Override
    public void getChosenWord(String lobbyCode, WordCallback callback) {
        DatabaseReference wordRef = database.getReference("lobbies/"+lobbyCode+"/word");
        wordRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String word = snapshot.getValue(String.class);
                    callback.onSuccess(word);
                } else {
                    callback.onFailure(new Exception("No word set yet"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    private String generateLobbyCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder b = new StringBuilder();
        for (int i=0; i<6; i++) b.append(chars.charAt((int)(Math.random()*chars.length())));
        return b.toString();
    }
}
