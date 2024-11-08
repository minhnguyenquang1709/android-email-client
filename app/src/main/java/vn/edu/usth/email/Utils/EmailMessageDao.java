package vn.edu.usth.email.Utils;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import vn.edu.usth.email.Model.EmailMessage;

@Dao
public interface EmailMessageDao {
    // query email message(s)
    @Query("SELECT * FROM EmailMessage WHERE userId = :userId")
    List<EmailMessage> getAll(String userId);


    @Query("SELECT * FROM EmailMessage WHERE messageId = :messageId LIMIT 1")
    EmailMessage getById(String messageId);

    // insert email message(s)
    @Insert
    void insert(EmailMessage emailMessage);

    @Insert
    void insertMany(List<EmailMessage> emailMessages);

    // delete email message(s)
    @Delete
    void delete(EmailMessage emailMessage);

    @Query("DELETE FROM EmailMessage")
    void deleteAll();
}
