package vn.edu.usth.email.Model;

public class Email {
    private String icon;
    private String senderName;
    private String snippet;
    private String time;
    private String messageId;

    public Email(String icon, String senderName, String snippet, String time, String messageId) {
        this.icon = icon;
        this.senderName = senderName;
        this.snippet = snippet;
        this.time = time;
        this.messageId = messageId;
    }

    public String getIcon() { return icon; }
    public String getSenderName() { return senderName; }
    public String getSnippet() { return snippet; }
    public String getTime() { return time; }
    public String getMessageId() { return messageId; }
}
