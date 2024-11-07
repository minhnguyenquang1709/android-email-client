package vn.edu.usth.email.Activity;


import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import vn.edu.usth.email.Adapter.EmailAdapter;
import vn.edu.usth.email.R;

import com.google.api.client.http.javanet.NetHttpTransport;


import vn.edu.usth.email.Model.Email;


public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private ImageButton searchButton;
    private RecyclerView recyclerView;
    private Gmail service;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        searchInput = findViewById(R.id.editTextSearch);
        searchButton = findViewById(R.id.search_btn);
        recyclerView = findViewById(R.id.search_recycle_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve passed data
        userId = getIntent().getStringExtra("userId");
        String accessToken = getIntent().getStringExtra("accessToken");

        try {
            service = initializeGmailApiService(accessToken);
        } catch (Exception e) {
            Log.e("SearchActivity", "Failed to initialize Gmail service", e);
        }
        // Initially fetch all emails
        fetchEmailsMessages(userId, service, "");

        // Set search button click listener
        searchButton.setOnClickListener(view -> {
            String searchTerm = searchInput.getText().toString().trim();
            EmailAdapter adapter = (EmailAdapter) recyclerView.getAdapter();
            if (adapter != null) {
                adapter.filter(searchTerm);  // Use local filtering
            }
        });
    }

    private Gmail initializeGmailApiService(String accessToken) throws GeneralSecurityException, IOException {
        final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
            request.getHeaders().setAuthorization("Bearer " + accessToken);
        }).setApplicationName("YourAppName").build();
    }
    private void fetchEmailsMessages(String userId, Gmail service, String searchTerm) {
        new Thread(() -> {
            List<Email> emailList = new ArrayList<>();
            try {
                // Set up the request, only applying a filter if searchTerm is not empty
                ListMessagesResponse listResponse = service.users().messages().list(userId).setQ(searchTerm.isEmpty() ? "" : searchTerm).execute();
                List<Message> messages = listResponse.getMessages();

                if (messages != null) {
                    for (Message message : messages) {
                        Message fullMessage = service.users().messages().get(userId, message.getId()).execute();

                        // Get the snippet
                        String snippet = fullMessage.getSnippet();

                        // Get sender's name or email from headers
                        String senderName = "Unknown Sender";
                        if (fullMessage.getPayload() != null && fullMessage.getPayload().getHeaders() != null) {
                            for (MessagePartHeader header : fullMessage.getPayload().getHeaders()) {
                                if ("From".equalsIgnoreCase(header.getName())) {
                                    senderName = header.getValue();
                                    break;
                                }
                            }
                        }

                        // Get sender's initial for the icon text
                        String iconText = senderName.isEmpty() ? "?" : senderName.substring(0, 1).toUpperCase();

                        // Format the internal date as time
                        long internalDate = fullMessage.getInternalDate();
                        String time = new SimpleDateFormat("h:mm a").format(new Date(internalDate));

                        // Create and add an Email object to the list
                        Email email = new Email(iconText, senderName, snippet, time);
                        emailList.add(email);
                    }

                    // Update RecyclerView on main thread
                    runOnUiThread(() -> {
                        EmailAdapter adapter = new EmailAdapter(this, emailList);
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

