package vn.edu.usth.email.Model;

// Email.java
public class Email {
    private String icon;
    private String title;
    private String snippet;
    private String time;

    public Email(String icon, String title, String snippet, String time) {
        this.icon = icon;
        this.title = title;
        this.snippet = snippet;
        this.time = time;
    }

    public String getIcon() { return icon; }
    public String getTitle() { return title; }
    public String getSnippet() { return snippet; }
    public String getTime() { return time; }
}
