package vn.edu.usth.email.Model;

import java.text.SimpleDateFormat;
import java.util.Date;

// Email.java
public class Email {
    private String icon;
    private String senderName;
    private String snippet;
    private String time;
    private String subject;

    public Email(String icon, String senderName, String snippet, String time, String subject) {
        this.icon = icon;
        this.senderName = senderName;
        this.snippet = snippet;
        this.time = time;
        this.subject = subject;
    }

    public Email(EmailMessage emailMessage, String icon){
        this.icon = icon;
        this.senderName = emailMessage.senderName;
        this.snippet = emailMessage.snippet;
        this.time = new SimpleDateFormat("h:mm a").format(new Date(emailMessage.originalSentDate));
        this.subject = emailMessage.subject;
    }

    public String getIcon() { return icon; }
    public String getSenderName() { return senderName; } // Renamed getter for sender name
    public String getSnippet() { return snippet; }
    public String getTime() { return time; }

    public String getSubject() {
        return subject;
    }
}