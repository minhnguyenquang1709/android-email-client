package vn.edu.usth.email.Model;

public class EmailItem {
    private String subject;
    private String username;
    private String emailAddr;
    private int timeSent;
    private String content;
    private boolean starred;
    private int profileImgId;

    public EmailItem(String username, String emailAddr, String subject, int timeSent, String content, int profileImgId){
        this.subject = subject;
        this.username = username;
        this.emailAddr = emailAddr;
        this.timeSent = timeSent;
        this.content = content;
        this.starred = false;
        this.profileImgId = profileImgId;
    }

    public String getSubject(){
        return this.subject;
    }

    public int getTimeSent(){
        return this.timeSent;
    }

    public String getContent(){
        return this.content;
    }

    public String getUsername() {
        return username;
    }

    public String getEmailAddr() {
        return emailAddr;
    }

    public int getProfileImgId() {
        return profileImgId;
    }

    public void setProfileImgId(int profileImgId) {
        this.profileImgId = profileImgId;
    }

    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTimeSent(int timeSent) {
        this.timeSent = timeSent;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }
}
