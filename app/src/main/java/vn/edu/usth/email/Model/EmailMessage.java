package vn.edu.usth.email.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.api.services.gmail.model.MessagePart;

import vn.edu.usth.email.Utils.Converter;

@Entity(primaryKeys = {"id", "messageId"})
public class EmailMessage {
    @NonNull
    public int id;
    @NonNull
    public String messageId;
    public String threadId;
    public String subject;
    public String snippet;
//    @TypeConverters({Converter.class})
//    public MessagePart payload;
    public String messageBody;
    public long originalSentDate;
    @NonNull
    public String senderName;
    @NonNull
    public String userId;
}

