package com.mygdx.game.android;

import com.google.firebase.database.*;
import com.mygdx.game.FirebaseInterface;
import java.util.HashMap;
import java.util.Map;

public class FirebaseManager implements FirebaseInterface {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference lobbyRef;
    private ValueEventListener lobbyListener;

    @Override
    public void createLobby(String hostName, LobbyCallback callback) {
        String lobbyCode = generateLobbyCode();
        lobbyRef = database.getReference("lobbies/" + lobbyCode);

        Map<String, Object> lobby = new HashMap<>();
        lobby.put("host", hostName);
        lobby.put("status", "waiting");
        lobby.put("createdAt", ServerValue.TIMESTAMP); // Auto-set Firebase timestamp
        lobby.put("players", new HashMap<String, Boolean>() {{
            put(hostName, true);
        }});

        // Atomic creation to avoid duplicates
        lobbyRef.setValue(lobby, (error, ref) -> {
            if (error == null) {
                callback.onSuccess(lobbyCode);
            } else {
                callback.onFailure(error.getMessage());
            }
        });
    }

    @Override
    public void joinLobby(String lobbyCode, String playerName, LobbyCallback callback) {
        DatabaseReference playersRef = database.getReference("lobbies/" + lobbyCode + "/players");

        // Check if lobby exists first
        playersRef.getParent().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.onFailure("Lobby not found");
                    return;
                }

                // Check if player name is already taken
                if (snapshot.child("players").hasChild(playerName)) {
                    callback.onFailure("Name already in use");
                    return;
                }

                // Join the lobby
                playersRef.child(playerName).setValue(true)
                    .addOnSuccessListener(__ -> callback.onSuccess(lobbyCode))
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    @Override
    public void startGame(String lobbyCode, String drawerName) {
        database.getReference("lobbies/" + lobbyCode)
            .child("status").setValue("drawing:" + drawerName);
    }

    @Override
    public void leaveLobby(String lobbyCode, String playerName) {
        DatabaseReference ref = database.getReference("lobbies/" + lobbyCode);
        ref.child("players").child(playerName).removeValue();
        // Host leaves? Delete lobby
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("host").getValue(String.class).equals(playerName)) {
                    ref.removeValue();
                }
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    @Override
    public void setupLobbyListener(String lobbyCode, LobbyStateCallback callback) {
        lobbyRef = database.getReference("lobbies/" + lobbyCode);
        lobbyListener = lobbyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.onLobbyClosed();
                    return;
                }

                String status = snapshot.child("status").getValue(String.class);
                if (status != null && status.startsWith("drawing:")) {
                    callback.onGameStarted(status.split(":")[1]);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onLobbyClosed();
            }
        });

        // Player join/leave listener
        lobbyRef.child("players").addChildEventListener(new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot snapshot, String prevKey) {
                callback.onPlayerJoined(snapshot.getKey());
            }
            @Override public void onChildRemoved(DataSnapshot snapshot) {
                callback.onPlayerLeft(snapshot.getKey());
            }
            @Override public void onChildChanged(DataSnapshot s, String p) {}
            @Override public void onChildMoved(DataSnapshot s, String p) {}
            @Override public void onCancelled(DatabaseError e) {}
        });
    }

    @Override
    public void initializeDatabaseStructure(Runnable onComplete) {
        // Just ensure the lobbies node exists
        database.getReference("lobbies").keepSynced(true);
        onComplete.run();
    }

    private String generateLobbyCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }
}
