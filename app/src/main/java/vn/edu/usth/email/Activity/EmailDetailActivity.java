package vn.edu.usth.email.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import vn.edu.usth.email.R;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import com.google.android.gms.auth.api.identity.AuthorizationRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.common.api.Scope;

public class EmailDetailActivity extends AppCompatActivity {

    private TextView senderNameView, snippetView, timeView;
    private String messageId; // Store message ID for deletion
    private Gmail service; // Gmail service
    private String accessToken; // Store accessToken if needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_detail);

        // Initialize views
        senderNameView = findViewById(R.id.sender_name);
        snippetView = findViewById(R.id.snippet);
        timeView = findViewById(R.id.time);
        ImageButton deleteButton = findViewById(R.id.delete_btn);
        ImageView backButton = findViewById(R.id.back_button);

        // Back button listener
        backButton.setOnClickListener(v -> onBackPressed());

        // Get data from intent
        Intent intent = getIntent();
        String senderName = intent.getStringExtra("senderName");
        String snippet = intent.getStringExtra("snippet");
        String time = intent.getStringExtra("time");
        messageId = intent.getStringExtra("messageId");
        accessToken = intent.getStringExtra("accessToken");

        // Set data to views
        senderNameView.setText(senderName);
        snippetView.setText(snippet);
        timeView.setText(time);

        // Initialize Gmail API service with modify scope
        try {
            service = initializeGmailApiService(accessToken);
            Log.d("EmailDetailActivity", "Received accessToken: " + accessToken);
        } catch (Exception e) {
            Log.e("EmailDetailActivity", "Failed to initialize Gmail service", e);
        }

        Log.d("EmailDetailActivity", "Retrieved Message ID: " + messageId);

        // Set delete button click listener with confirmation dialog
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageId != null) {
                    Log.d("EmailDetailActivity", "Message ID: " + messageId);
                    showDeleteConfirmationDialog();
                } else {
                    Log.e("EmailDetailActivity", "Message ID is null");
                }
            }
        });
    }

    // Show confirmation dialog before deleting
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this email?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (messageId != null && service != null) {
                        deleteEmail(messageId);  // Delete email if confirmed
                    } else {
                        Toast.makeText(this, "Unable to delete email.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Method to initialize Gmail API
    private Gmail initializeGmailApiService(String accessToken) throws GeneralSecurityException, IOException {
        final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
        final com.google.api.client.http.javanet.NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
            request.getHeaders().setAuthorization("Bearer " + accessToken);
        }).setApplicationName("YourAppName").build();
    }

    // Method to delete an email using Gmail API
    private void deleteEmail(String messageId) {
        new Thread(() -> {
            try {
                service.users().messages().trash("me", messageId).execute();

                Log.i("EmailDetailActivity", "Email deleted successfully");

                // Return to previous activity after deletion
                runOnUiThread(() -> {
                    Toast.makeText(this, "Email deleted", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            } catch (IOException e) {
                Log.e("EmailDetailActivity", "Failed to delete email", e);
                runOnUiThread(() -> Toast.makeText(this, "Failed to delete email", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


}
