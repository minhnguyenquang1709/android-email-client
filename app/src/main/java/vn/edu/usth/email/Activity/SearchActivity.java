package vn.edu.usth.email.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import vn.edu.usth.email.Adapter.EmailAdapter;
import vn.edu.usth.email.Helper.GmailServiceHelper;
import vn.edu.usth.email.Helper.NavigationHelper;
import vn.edu.usth.email.Model.Email;
import vn.edu.usth.email.R;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private ImageButton searchButton;
    private TextView avatar;
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
        avatar = findViewById(R.id.avatar_icon);
        recyclerView = findViewById(R.id.search_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                    NavigationHelper.navigateToActivity(SearchActivity.this, StarredActivity.class, userId, accessToken);
                } else if (itemId == R.id.nav_sent) {
                    NavigationHelper.navigateToActivity(SearchActivity.this, SendActivity.class, userId, accessToken);
                } else if (itemId == R.id.trash) {
                    NavigationHelper.navigateToActivity(SearchActivity.this, TrashActivity.class, userId, accessToken);
                } else if (itemId == R.id.helpnfeedback) {
                    startActivity(new Intent(SearchActivity.this, HelpFeedbackActivity.class));
                } else if (itemId == R.id.compose){
                    NavigationHelper.navigateToActivity(SearchActivity.this, WriteActivity.class, userId, accessToken);
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.mail_icon) {
                if (!(this instanceof SearchActivity)) {
                    startActivity(new Intent(SearchActivity.this, SearchActivity.class));
                }
            } else if (itemId == R.id.settings_icon) {
                startActivity(new Intent(SearchActivity.this, GeneralSettingActivity.class));
            }
            return true;
        });

        // Open drawer with menu button
        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        // Retrieve passed data
        userId = getIntent().getStringExtra("userId");
        accessToken = getIntent().getStringExtra("accessToken");

        // set avatar icon
        avatar.setText(userId.substring(0,1).toUpperCase());

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

    // Fetch emails based on search term
    private void fetchEmailsMessages(String userId, Gmail service, String searchTerm) {
        new Thread(() -> {
            List<Email> emailList = new ArrayList<>();
            try {
                // Fetch messages with search term
                ListMessagesResponse listResponse = service.users().messages().list(userId).setQ(searchTerm).execute();
                List<Message> messages = listResponse.getMessages();

                if (messages != null) {
                    for (Message message : messages) {
                        // Retrieve full message details
                        Message fullMessage = service.users().messages().get(userId, message.getId()).execute();
                        String snippet = fullMessage.getSnippet();
                        String senderName = "Unknown Sender";
                        String subject = "No Subject";

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

                        // Create Email object and add to list
                        Email email = new Email(iconText, senderName, snippet, time, messageId, subject);
                        emailList.add(email);
                    }

                    runOnUiThread(() -> {
                        // Set up EmailAdapter with fetched emails
                        EmailAdapter adapter = new EmailAdapter(this, emailList, accessToken);
                        adapter.setOnItemClickListener((senderName, snippet, time, messageId, subject) -> {
                            Intent intent = new Intent(SearchActivity.this, EmailDetailActivity.class);
                            intent.putExtra("senderName", senderName);
                            intent.putExtra("snippet", snippet);
                            intent.putExtra("time", time);
                            intent.putExtra("messageId", messageId);
                            intent.putExtra("accessToken", accessToken);
                            intent.putExtra("subject", subject); // Pass subject to EmailDetailActivity
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fetchEmailsMessages(userId, service, "");
        }
    }
}
