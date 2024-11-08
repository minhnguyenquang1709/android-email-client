package vn.edu.usth.email.Activity;


import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import vn.edu.usth.email.Adapter.EmailAdapter;
import vn.edu.usth.email.EmailClient;
import vn.edu.usth.email.Model.Email;
import vn.edu.usth.email.Model.EmailMessage;
import vn.edu.usth.email.R;
import vn.edu.usth.email.Utils.EmailMessageDao;


public class HomeActivity extends AppCompatActivity {

    private EditText searchInput;
    private ImageButton searchButton;
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private Gmail service;
    private String userId;
    private String accessToken;
    private Handler handler;
    private EmailMessageDao emailMessageDao;

    private EmailAdapter emailAdapter;

    public HomeActivity(){
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
        emailAdapter = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("HomeActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Retrieve passed data
        userId = getIntent().getStringExtra("userId");
        accessToken = getIntent().getStringExtra("accessToken");

        // Initialize views
        searchInput = findViewById(R.id.editTextSearch);
        searchButton = findViewById(R.id.search_btn);
        recyclerView = findViewById(R.id.search_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // after setting adapter
        emailAdapter = new EmailAdapter(HomeActivity.this, new ArrayList<>(), accessToken);
        recyclerView.setAdapter(emailAdapter);
        drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton menuButton = findViewById(R.id.icon_menu);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set up hamburger icon to open drawer
        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // setup database access point
        emailMessageDao = EmailClient.getDatabase().emailMessageDao();

        // setup Navigation Drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.Starred) {
                    startStarredActivity(userId, accessToken);
                } else if (itemId == R.id.nav_sent) {
                    startSendActivity(userId, accessToken);
                } else if (itemId == R.id.trash) {
                    startTrashActivity(userId, accessToken);
                } else if (itemId == R.id.helpnfeedback) {
                    startActivity(new Intent(HomeActivity.this, HelpFeedbackActivity.class));
                }

                // Close the drawer after an item is clicked
                drawerLayout.closeDrawers();
                return true;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Navigate to SearchActivity only if it's not already the current activity
            if (itemId == R.id.mail_icon) {
                // If SearchActivity is already open, avoid opening it again
                if (!(this instanceof HomeActivity)) {
                    startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                }
            } else if (itemId == R.id.video_icon) {
                startActivity(new Intent(HomeActivity.this, GeneralSettingActivity.class));
            }
            return true;
        });

        // Set up drawer open with hamburger icon
        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        try {
            service = initializeGmailApiService(accessToken);
        } catch (Exception e) {
            Log.e("HomeActivity", "Failed to initialize Gmail service", e);
        }

        // Initially fetch all emails and put to database
        if (service != null){
            fetchEmailsMessages(userId, service, handler);
        }else{
            Log.e("Gmail Service", "No service instance");
        }
        // searchEmailsMessages(userId, service, "");

        // Set search button click listener
        searchButton.setOnClickListener(view -> {
            String searchTerm = searchInput.getText().toString().trim();
            EmailAdapter adapter = (EmailAdapter) recyclerView.getAdapter();
            if (adapter != null) {
                adapter.filter(searchTerm);  // Use local filtering
            }
        });
    }

    @Override
    protected void onStart() {
        Log.i("HomeActivity", "onStart");
        super.onStart();
        getAllEmailMessages();
    }

    // Define separate methods for each activity to start
    private void startStarredActivity(String userId, String accessToken) {
        Intent intent = new Intent(HomeActivity.this, StarredActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("accessToken", accessToken);
        startActivity(intent);
    }

    private void startSendActivity(String userId, String accessToken) {
        Intent intent = new Intent(HomeActivity.this, SendActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("accessToken", accessToken);
        startActivity(intent);
    }

    private void startTrashActivity(String userId, String accessToken) {
        Intent intent = new Intent(HomeActivity.this, TrashActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("accessToken", accessToken);
        startActivity(intent);
    }

    // Refresh data when returning from EmailDetailActivity after deletion
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            searchEmailsMessages(userId, service, "");
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
                        emailMessage.userId = userId;

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

                        // get original sent date, subject, sender's name
                        long originalSentDate = 0;
                        emailMessage.originalSentDate = originalSentDate;
                        String subject = null;
                        String senderName = null;
                        if (headers != null && !headers.isEmpty()){
                            for (MessagePartHeader header: headers){
                                if ( subject != null && senderName != null){
                                    emailMessage.subject = subject;
                                    emailMessage.senderName = senderName;
                                    break;
                                }

                                if(header.getName().equalsIgnoreCase("Subject")){
                                    subject = header.getValue();
                                }
                                if(header.getName().equalsIgnoreCase("From")){
                                    senderName = header.getValue();
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

                    getAllEmailMessages();

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

    // add all obtained email messages to the local database
    private void addToDatabase(EmailMessage emailMessage, Handler handler){
        Log.i("addToDatabase", "start adding obtained messages to Room database");

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
    private void searchEmailsMessages(String userId, Gmail service, String searchTerm) {
        new Thread(() -> {
            List<Email> emailList = new ArrayList<>();
            try {
                // Set up the request, only applying a filter if searchTerm is not empty
                ListMessagesResponse listResponse = service.users().messages().list(userId).setQ(searchTerm.isEmpty() ? "" : searchTerm).execute();
                List<Message> messages = listResponse.getMessages();

                if (messages != null) {
                    for (Message message : messages) {
                        Message fullMessage = service.users().messages().get(userId, message.getId()).execute();
                        MessagePart payload = fullMessage.getPayload();
                        List<MessagePartHeader> headers = payload.getHeaders();

                        // Get the snippet
                        String snippet = fullMessage.getSnippet();

                        // Get sender's name, subject from headers
                        String senderName = "Unknown Sender";
                        String subject = null;
                        if (fullMessage.getPayload() != null && headers != null) {
                            for (MessagePartHeader header : fullMessage.getPayload().getHeaders()) {
                                if(senderName != null && subject != null){
                                    break;
                                }

                                if ("From".equalsIgnoreCase(header.getName())) {
                                    senderName = header.getValue();
                                }
                                if(header.getName().equalsIgnoreCase("Subject")){
                                    subject = header.getValue();
                                }
                            }
                        }

                        // Get sender's initial for the icon text
                        String iconText = senderName.isEmpty() ? "?" : senderName.substring(0, 1).toUpperCase();

                        // Format the internal date as time
                        long internalDate = fullMessage.getInternalDate();
                        String time = new SimpleDateFormat("h:mm a").format(new Date(internalDate));

                        // Create and add an Email object to the list
                        Email email = new Email(iconText, senderName, snippet, time, subject);
                        emailList.add(email);
                    }

                    // Update RecyclerView on main thread
                    runOnUiThread(() -> {
                        EmailAdapter adapter = new EmailAdapter(this, emailList, accessToken);
                        recyclerView.setAdapter(adapter);
                    });
                } else {
                    Log.i("HomeActivity", "No messages found.");
                }
            } catch (IOException e) {
                Log.e("HomeActivity", "Error fetching emails", e);
            }
        }).start();
    }

    private List<Email> getAllEmailMessages(){
        List<Email> emails = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<EmailMessage> emailMessageList = emailMessageDao.getAll(userId);

                for (EmailMessage emailMessage: emailMessageList){
                    String iconText = emailMessage.senderName.isEmpty() ? "?" : emailMessage.senderName.substring(0, 1).toUpperCase();
                    emails.add(new Email(emailMessage, iconText));
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // update the UI with the fetched emails
                        if (emails.isEmpty()) {
                            Log.e("Get messages from database", "No message!");
                        } else {
                            Log.i("Set EmailAdapter", "Email messages from database detected, setting the adapter for RecyclerView");
                            emailAdapter.updateOriginalEmails(emails);
                        }
                    }
                });
            }
        });
        t.start();

        return emails;
    }
}

