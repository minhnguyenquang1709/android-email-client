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
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vn.edu.usth.email.Adapter.EmailAdapter;
import vn.edu.usth.email.R;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import vn.edu.usth.email.Model.Email;
import vn.edu.usth.email.R;

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
        searchInput = findViewById(R.id.search_input);
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

        // Set search button click listener
        searchButton.setOnClickListener(view -> {
            String searchTerm = searchInput.getText().toString().trim();
            if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                searchInput.setError("Please enter at least 3 characters");
            } else {
                fetchEmailsMessages(userId, service, searchTerm);
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
        Log.i("SearchActivity", "Starting email search for query: " + searchTerm);
        new Thread(() -> {
            List<Email> emailList = new ArrayList<>();
            try {
                ListMessagesResponse listResponse = service.users().messages().list(userId).setQ(searchTerm).execute();
                List<Message> messages = listResponse.getMessages();

                if (messages != null) {
                    for (Message message : messages) {
                        Message fullMessage = service.users().messages().get(userId, message.getId()).execute();

                        // Extract data for display
                        String title = fullMessage.getSnippet(); // Just an example, adjust as needed
                        String snippet = fullMessage.getSnippet();

                        // Extract sender’s initial or any identifier for icon
                        String iconText = title != null && !title.isEmpty() ? title.substring(0, 1).toUpperCase() : "B";

                        // Format the internal date
                        long internalDate = fullMessage.getInternalDate();
                        String time = new SimpleDateFormat("h:mm a").format(new Date(internalDate));

                        // Create email item and add to list
                        Email email = new Email(iconText, title, snippet, time);
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
