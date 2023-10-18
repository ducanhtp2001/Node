package com.example.note.SQLite;

import android.content.Context;

public class ConnectSharing {
    private static Connect connect;

    public ConnectSharing() {
    }

    public static synchronized Connect getConnectSharing(Context context){
        if(connect == null) {
            connect = new Connect(context.getApplicationContext(), "trash", null, 1);
        }
        return connect;
    }

}
