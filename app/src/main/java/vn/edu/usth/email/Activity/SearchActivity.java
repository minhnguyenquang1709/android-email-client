package vn.edu.usth.email.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import vn.edu.usth.email.Adapter.EmailAdapter;
import vn.edu.usth.email.Helper.GmailServiceHelper;
import vn.edu.usth.email.Model.Email;
import vn.edu.usth.email.R;

public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private ImageButton searchButton;
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private Gmail service;
    private String userId;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        searchInput = findViewById(R.id.editTextSearch);
        searchButton = findViewById(R.id.search_btn);
        recyclerView = findViewById(R.id.search_recycle_view);

        drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton menuButton = findViewById(R.id.icon_menu);

        NavigationView navigationView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up Navigation Drawer
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
                    startActivity(new Intent(SearchActivity.this, HelpFeedbackActivity.class));
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
                if (!(this instanceof SearchActivity)) {
                    startActivity(new Intent(SearchActivity.this, SearchActivity.class));
                }
            } else if (itemId == R.id.video_icon) {
                startActivity(new Intent(SearchActivity.this, GeneralSettingActivity.class));
            }
            return true;
        });

        // Set up drawer open with hamburger icon
        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve passed data
        userId = getIntent().getStringExtra("userId");
        accessToken = getIntent().getStringExtra("accessToken");

        // Initialize Gmail service
        try {
            service = GmailServiceHelper.getService(accessToken);
        } catch (Exception e) {
            Log.e("SearchActivity", "Failed to initialize Gmail service", e);
        }

        // Fetch emails initially
        fetchEmailsMessages(userId, service, "");

        // Set search button click listener
        searchButton.setOnClickListener(view -> {
            String searchTerm = searchInput.getText().toString().trim();
            fetchEmailsMessages(userId, service, searchTerm);
        });
    }

    // Define separate methods for each activity to start
    private void startStarredActivity(String userId, String accessToken) {
        Intent intent = new Intent(SearchActivity.this, StarredActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("accessToken", accessToken);
        startActivity(intent);
    }

    private void startSendActivity(String userId, String accessToken) {
        Intent intent = new Intent(SearchActivity.this, SendActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("accessToken", accessToken);
        startActivity(intent);
    }

    private void startTrashActivity(String userId, String accessToken) {
        Intent intent = new Intent(SearchActivity.this, TrashActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("accessToken", accessToken);
        startActivity(intent);
    }

    // Refresh data when returning from EmailDetailActivity after deletion
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fetchEmailsMessages(userId, service, "");
        }
    }



    // Fetch emails based on search term
    private void fetchEmailsMessages(String userId, Gmail service, String searchTerm) {
        new Thread(() -> {
            List<Email> emailList = new ArrayList<>();
            try {
                ListMessagesResponse listResponse = service.users().messages().list(userId).setQ(searchTerm.isEmpty() ? "" : searchTerm).execute();
                List<Message> messages = listResponse.getMessages();

                if (messages != null) {
                    for (Message message : messages) {
                        Message fullMessage = service.users().messages().get(userId, message.getId()).execute();
                        String snippet = fullMessage.getSnippet();

                        // Fetch sender name
                        String senderName = "Unknown Sender";
                        String subject = "No Subject"; // Default subject in case it's missing
                        if (fullMessage.getPayload() != null && fullMessage.getPayload().getHeaders() != null) {
                            for (MessagePartHeader header : fullMessage.getPayload().getHeaders()) {
                                if ("From".equalsIgnoreCase(header.getName())) {
                                    senderName = header.getValue();
                                } else if ("Subject".equalsIgnoreCase(header.getName())) {
                                    subject = header.getValue();
                                }
                            }
                        }

                        String iconText = senderName.isEmpty() ? "?" : senderName.substring(0, 1).toUpperCase();
                        long internalDate = fullMessage.getInternalDate();
                        String time = new SimpleDateFormat("h:mm a").format(new Date(internalDate));
                        String messageId = message.getId();

                        // Include subject when creating the Email object
                        Email email = new Email(iconText, senderName, snippet, time, messageId, subject);
                        emailList.add(email);
                    }

                    runOnUiThread(() -> {
                        EmailAdapter adapter = new EmailAdapter(this, emailList, accessToken);
                        adapter.setOnItemClickListener((senderName, snippet, time, messageId, subject) -> {
                            Intent intent = new Intent(SearchActivity.this, EmailDetailActivity.class);
                            intent.putExtra("senderName", senderName);
                            intent.putExtra("snippet", snippet);
                            intent.putExtra("time", time);
                            intent.putExtra("messageId", messageId);
                            intent.putExtra("accessToken", accessToken);
                            intent.putExtra("subject", subject); // Pass the subject to EmailDetailActivity
                            startActivityForResult(intent, 1);
                        });
                        recyclerView.setAdapter(adapter);
                    });
                } else {
                    Log.i("SearchActivity", "No messages found.");
                }
            } catch (IOException e) {
                Log.e("SearchActivity", "Error fetching emails", e);
            }
        }).start();
    }

}