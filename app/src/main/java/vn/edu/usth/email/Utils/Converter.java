package vn.edu.usth.email.Utils;

import androidx.room.TypeConverter;

import com.google.api.services.gmail.model.MessagePart;
import com.google.gson.Gson;

public class Converter {
    @TypeConverter
    public static MessagePart fromString(String value){
        return new Gson().fromJson(value, MessagePart.class);
    }

    @TypeConverter
    public static String fromMessagePart(MessagePart part){
        return new Gson().toJson(part);
    }
}
