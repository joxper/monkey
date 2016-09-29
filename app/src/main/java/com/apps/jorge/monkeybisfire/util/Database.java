package com.apps.jorge.monkeybisfire.util;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by jorge on 9/28/16.
 */

public class Database {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
