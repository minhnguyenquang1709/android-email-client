package vn.edu.usth.email.Model;

public class Email {
    private String iconText;
    private String senderName;
    private String snippet;
    private String time;
    private String messageId;
    private String subject;

    public Email(String iconText, String senderName, String snippet, String time, String messageId, String subject) {
        this.iconText = iconText;
        this.senderName = senderName;
        this.snippet = snippet;
        this.time = time;
        this.messageId = messageId;
        this.subject = subject;
    }

    // Getter for iconText (renamed to getIcon for consistency in adapter)
    public String getIcon() {
        return iconText;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getTime() {
        return time;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSubject() {
        return subject;
    }
}
