package com.rooms4rent.sell.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHandler {

    public FirebaseHandler() {
        
    }

    public DatabaseReference getFirebaseConnection(String tableName) {
        return FirebaseDatabase.getInstance().getReference(tableName);
    }
}
