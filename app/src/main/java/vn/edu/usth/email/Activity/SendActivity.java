package vn.edu.usth.email.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import vn.edu.usth.email.Adapter.EmailAdapter;
import vn.edu.usth.email.Model.Email;
import vn.edu.usth.email.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Gmail service;
    private String userId;
    private String accessToken;
    private EmailAdapter adapter;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        recyclerView = findViewById(R.id.sent_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton menuButton = findViewById(R.id.icon_menu);

        userId = getIntent().getStringExtra("userId");
        accessToken = getIntent().getStringExtra("accessToken");

        try {
            service = initializeGmailApiService(accessToken);
        } catch (Exception e) {
            Log.e("SendActivity", "Failed to initialize Gmail service", e);
            return;
        }

        fetchEmails("label:sent");

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.search) {
                    startActivity(new Intent(SendActivity.this, SearchActivity.class));
                }else if (itemId == R.id.Starred) {
                    startActivity(new Intent(SendActivity.this, StarredActivity.class));
                } else if (itemId == R.id.nav_sent) {
                    startActivity(new Intent(SendActivity.this, SendActivity.class));
                } else if (itemId == R.id.trash) {
                    startActivity(new Intent(SendActivity.this, TrashActivity.class));
                } else if (itemId == R.id.helpnfeedback) {
                    startActivity(new Intent(SendActivity.this, HelpFeedbackActivity.class));
                }

                // Close the drawer after an item is clicked
                drawerLayout.closeDrawers();
                return true;
            }
        });
        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private Gmail initializeGmailApiService(String accessToken) throws Exception {
        return new Gmail.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                request -> request.getHeaders().setAuthorization("Bearer " + accessToken))
                .setApplicationName("YourAppName")
                .build();
    }

    private void fetchEmails(String query) {
        new Thread(() -> {
            List<Email> emailList = new ArrayList<>();
            try {
                ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();
                List<Message> messages = response.getMessages();

                if (messages != null) {
                    for (Message message : messages) {
                        Message fullMessage = service.users().messages().get(userId, message.getId()).execute();
                        String snippet = fullMessage.getSnippet();
                        String senderName = getSenderName(fullMessage);

                        String iconText = senderName.isEmpty() ? "?" : senderName.substring(0, 1).toUpperCase();
                        String time = "";  // Placeholder for time, if needed

                        emailList.add(new Email(iconText, senderName, snippet, time, fullMessage.getId()));
                    }

                    runOnUiThread(() -> {
                        adapter = new EmailAdapter(this, emailList, accessToken);
                        recyclerView.setAdapter(adapter);
                    });
                } else {
                    Log.i("SendActivity", "No sent emails found.");
                }
            } catch (IOException e) {
                Log.e("SendActivity", "Error fetching sent emails", e);
            }
        }).start();
    }

    private String getSenderName(Message message) {
        for (MessagePartHeader header : message.getPayload().getHeaders()) {
            if (header.getName().equalsIgnoreCase("From")) {
                return header.getValue();
            }
        }
        return "Unknown Sender";
    }
}