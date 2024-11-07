package vn.edu.usth.email.Model;

// Email.java
public class Email {
    private String icon;
    private String senderName;
    private String snippet;
    private String time;

    public Email(String icon, String senderName, String snippet, String time) {
        this.icon = icon;
        this.senderName = senderName;
        this.snippet = snippet;
        this.time = time;
    }

    public String getIcon() { return icon; }
    public String getSenderName() { return senderName; } // Renamed getter for sender name
    public String getSnippet() { return snippet; }
    public String getTime() { return time; }
}