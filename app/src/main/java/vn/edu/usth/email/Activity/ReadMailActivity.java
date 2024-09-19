package vn.edu.usth.email.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.edu.usth.email.R;

public class ReadMailActivity extends AppCompatActivity {
    Toolbar toolbar;
    String username;
    String subject;
    int timeSent;
    String emailAddr;
    int profileImgId;
    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read_mail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.readMail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        subject = intent.getStringExtra("subject");
        timeSent = intent.getIntExtra("timeSent", 0);
        emailAddr = intent.getStringExtra("emailAddr");
        profileImgId = intent.getIntExtra("profileImgId", 0);
        content = intent.getStringExtra("content");

        // setup action bar

    }
}