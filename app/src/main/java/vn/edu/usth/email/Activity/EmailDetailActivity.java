package vn.edu.usth.email.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import vn.edu.usth.email.R;

public class EmailDetailActivity extends AppCompatActivity {

    private TextView senderNameView, snippetView, timeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_detail);


        // Back button click listener
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        // Initialize views
        senderNameView = findViewById(R.id.sender_name);
        snippetView = findViewById(R.id.snippet);
        timeView = findViewById(R.id.time);

        // Get data from intent
        Intent intent = getIntent();
        String senderName = intent.getStringExtra("senderName");
        String snippet = intent.getStringExtra("snippet");
        String time = intent.getStringExtra("time");

        // Set data to views
        senderNameView.setText(senderName);
        snippetView.setText(snippet);
        timeView.setText(time);
    }
}
