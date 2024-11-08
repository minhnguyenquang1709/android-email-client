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
import java.io.IOException;
import java.security.GeneralSecurityException;
import vn.edu.usth.email.R;
import android.view.View;
import androidx.appcompat.app.AlertDialog;

public class EmailDetailActivity extends AppCompatActivity {

    private TextView senderNameView, snippetView, timeView;
    private String messageId;
    private Gmail service;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_detail);

        senderNameView = findViewById(R.id.sender_name);
        snippetView = findViewById(R.id.snippet);
        timeView = findViewById(R.id.time);
        ImageButton deleteButton = findViewById(R.id.delete_btn);
        ImageView backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        String senderName = intent.getStringExtra("senderName");
        String snippet = intent.getStringExtra("snippet");
        String time = intent.getStringExtra("time");
        messageId = intent.getStringExtra("messageId");
        accessToken = intent.getStringExtra("accessToken");

        senderNameView.setText(senderName);
        snippetView.setText(snippet);
        timeView.setText(time);

        try {
            service = initializeGmailApiService(accessToken);
        } catch (Exception e) {
            Log.e("EmailDetailActivity", "Failed to initialize Gmail service", e);
        }

        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this email?")
                .setPositiveButton("Yes", (dialog, which) -> deleteEmail(messageId))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private Gmail initializeGmailApiService(String accessToken) throws GeneralSecurityException, IOException {
        final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
        final com.google.api.client.http.javanet.NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
            request.getHeaders().setAuthorization("Bearer " + accessToken);
        }).setApplicationName("YourAppName").build();
    }

    private void deleteEmail(String messageId) {
        new Thread(() -> {
            try {
                service.users().messages().trash("me", messageId).execute();
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
