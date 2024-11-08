package vn.edu.usth.email;

import android.app.Application;

import androidx.room.Room;

import vn.edu.usth.email.Utils.AppDatabase;

public class EmailClient extends Application {
    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize Room database
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "email_database").build();
    }

    public static AppDatabase getDatabase(){
        return database;
    }
}
