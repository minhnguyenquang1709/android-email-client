package vn.edu.usth.email.Activity;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import vn.edu.usth.email.EmailClient;
import vn.edu.usth.email.Model.EmailMessage;
import vn.edu.usth.email.R;
import vn.edu.usth.email.Utils.EmailMessageDao;

public class HomeActivity2 extends AppCompatActivity {
    private String accessToken;
    private Gmail service;
    private final Handler handler;
    private EmailMessageDao emailMessageDao;
    private String userId;

    public HomeActivity2() {
        Log.i("HomeActivity2", "HomeActivity2 constructor");

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                // executed in the main thread
                String content = msg.getData().toString();
                Log.i("HomeActivity2 Handler", content);
            }
        };

        accessToken = null;
        service = null;
        emailMessageDao = null;
        userId = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getStringExtra("email");
        accessToken = getIntent().getStringExtra("accessToken");
        emailMessageDao = EmailClient.getDatabase().emailMessageDao();
        if(emailMessageDao == null){
            Log.e("HomeActivityError", "Unable to get database!");
        }

        try {

            service = initializeGmailApiService(accessToken);
            Log.i("Gmail Service", "Initialized Gmail service instance");

            if(service == null){
                Log.e("Gmail Service", "No service instance");
            }else{
                // successfully created a Gmail instance to handle an account
                String userId = getIntent().getStringExtra("email");
                fetchEmailsMessages(userId, service, handler);
            }

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // create a Gmail service instance
    private Gmail initializeGmailApiService(String accessToken) throws GeneralSecurityException, IOException {
        Log.i("GmailAPI", "start getting email data...");
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();;

        Gmail service;
        service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
            request.setInterceptor(httpRequest->{
                // set the access token in the request header
                httpRequest.getHeaders().setAuthorization("Bearer " + accessToken);
            });
        }).build();

        return service;
    }

    // get all email messages
    private void fetchEmailsMessages(String userId, Gmail service, Handler handler){
        Log.i("fetching", "start getting email messages");
        Thread bgThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // get email messages
                ListMessagesResponse listResponse = null;
                try {
                    listResponse = service.users().messages().list(userId).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                List<Message> messages = listResponse.getMessages();

                if(messages == null || messages.isEmpty()){
                    Log.i("GmailAPI", "No messages found.");
                }else{
//                    Log.i("GmailAPI", "Messages:");

                    for (Message message: messages){
                        Message fullMessage = null;
                        EmailMessage emailMessage = new EmailMessage();

                        emailMessage.messageId = message.getId();
                        emailMessage.threadId = message.getThreadId();

                        try {
                            fullMessage = service.users().messages().get(userId, message.getId()).execute();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (fullMessage == null){
                            Log.e("GmailAPI Error", "Cannot get the full message");
                            return;
                        }

                        // get the payload
                        MessagePart payload = fullMessage.getPayload();
                        List<MessagePartHeader> headers = payload.getHeaders();

                        // get snippet
//                        Log.i("GmailAPI", "snippet: " + fullMessage.getSnippet());
                        emailMessage.snippet = fullMessage.getSnippet();

                        // get avatar
                        // String avatarUri = message.get

                        // get original sent date and subject
                        long originalSentDate = 0;
                        originalSentDate = fullMessage.getInternalDate();
                        String subject = null;
                        if (headers != null && !headers.isEmpty()){
                            for (MessagePartHeader header: headers){
                                if (originalSentDate != 0 && subject != null){
//                                    Log.i("Email message", "Original sent date: " + originalSentDate);
//                                    Log.i("Email message", "Subject: " + subject);

                                    emailMessage.originalSentDate = originalSentDate;
                                    emailMessage.subject = subject;
                                    break;
                                }


                                if(header.getName().equalsIgnoreCase("Subject")){
                                    subject = header.getValue();
                                }
                            }
                        }

                        if (payload != null){
                            // get the parts of the message (if it's multipart)
                            List<MessagePart> parts = payload.getParts();

                            // get the message body
                            if (parts == null || parts.isEmpty()) {
                                // Get the body of the message directly
                                MessagePartBody body = payload.getBody();
                                if (body != null){
                                    String messageBody = new String(payload.getBody().decodeData());
//                                    Log.i("Email message","No subparts");


                                } else{
                                    Log.e("GmailAPI", "Message body is null.");
                                }
                            } else {
                                // If the message is multipart, iterate through the parts
                                for (MessagePart part : parts) {
                                    if (part != null){
//                                        Log.i("Email message", "Part " + parts.indexOf(part));
                                        String mimeType = part.getMimeType();
                                        // Check if the part is text/plain or text/html
                                        if (mimeType.equals("text/plain")) {
                                            String body = new String(part.getBody().decodeData());
//                                            Log.i("Email message", "Plain Text Body: " + body);
//                                            Log.i("Email message", "Plain Text Body");
                                        } else if (mimeType.equals("text/html")) {
                                            String htmlBody = new String(part.getBody().decodeData());
//                                            Log.i("Email message", "HTML Body: " + htmlBody);
                                            emailMessage.messageBody = htmlBody;
                                        }
                                    } else{
                                        Log.e("GmailAPI", "Part body is null for part: ");
                                    }
                                }
                            }
                        } else{
                            Log.e("GmailAPI", "No payload");
//                            Log.i("GmailAPI", message.decodeRaw());
                        }
                        // add the new email message to database
                        addToDatabase(emailMessage, handler);
                    }



                    // send a message to the main thread
                    Bundle bundle = new Bundle();
                    bundle.putString("server_response", "check logcat");
                    android.os.Message msg = new android.os.Message();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }
        });
        bgThread.start();
    }

    private void addToDatabase(EmailMessage emailMessage, Handler handler){
        if (emailMessage == null) return;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (emailMessageDao == null) {
                    Log.e("HomeActivity2 Error", "No database detected");
                    return;
                }else{
                    try {
                        emailMessageDao.insert(emailMessage);
                        Log.i("HomeActivity2 Database", "added an email message to local database!");
                    }catch (SQLiteConstraintException e){
                        Log.e("Database Error", "Duplicate primary key: " + e.getMessage());
                    }

                    // send message to the main thread
                    Bundle bundle = new Bundle();
                    bundle.putString("addToDatabase", "done add-to-db thread");
                    android.os.Message msg = new android.os.Message();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }
        });

        t.start();
    }
}