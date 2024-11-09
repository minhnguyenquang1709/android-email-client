package vn.edu.usth.email.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"id", "messageId"})
public class EmailMessage {
    @NonNull
    public int id;
    @NonNull
    public String messageId;
    public String threadId;
    public String subject;
    public String snippet;

    public String messageBody;
    public long originalSentDate;
    @NonNull
    public String senderName;
    @NonNull
    public String userId;
}

