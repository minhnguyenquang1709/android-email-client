package vn.edu.usth.email.Utils;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import vn.edu.usth.email.Model.EmailMessage;

@Database(entities = {EmailMessage.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EmailMessageDao emailMessageDao();
}
