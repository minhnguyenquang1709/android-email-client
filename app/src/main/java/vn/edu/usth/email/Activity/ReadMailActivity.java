package vn.edu.usth.email.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.edu.usth.email.R;

public class ReadMailActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageButton backBtn;
    ImageButton archiveBtn;
    ImageButton moreOptionsBtn;
    String username;
    String subject;
    int timeSent;
    String emailAddr;
    int profileImgId;
    String content;

    ImageView profileImgView;
    TextView usernameView;
    TextView timeSentView;
    TextView emailAddrView;
    TextView subjectView;
    TextView contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read_mail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_read_mail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get view
        profileImgView = findViewById(R.id.profileImage);
        usernameView = findViewById(R.id.userName);
        timeSentView = findViewById(R.id.timeAgo);
        emailAddrView = findViewById(R.id.userEmail);
        subjectView = findViewById(R.id.email_subject);
        contentView = findViewById(R.id.content);

        // get data
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        subject = intent.getStringExtra("subject");
        timeSent = intent.getIntExtra("timeSent", 0);
        emailAddr = intent.getStringExtra("emailAddr");
        profileImgId = intent.getIntExtra("profileImg", 0);
        content = intent.getStringExtra("content");

        // convert timeSent
        String timeSentText;
        if (timeSent >= 60){
            timeSentText = timeSent/60 + "m";
        }else{
            timeSentText = timeSent + "s";
        }

        // Log the received data
        Log.i("ReadMailActivity", "profileImgId="+profileImgId);

        // apply data to view
        profileImgView.setImageResource(profileImgId);
        usernameView.setText(username);
        timeSentView.setText(timeSentText);
        emailAddrView.setText(emailAddr);
        subjectView.setText(subject);
        contentView.setText(content);

        // setup action bar
        toolbar = findViewById(R.id.appbar_read_mail);
        setSupportActionBar(toolbar);
        backBtn = toolbar.findViewById(R.id.button_back);
        archiveBtn = toolbar.findViewById(R.id.button_archive);
        moreOptionsBtn = toolbar.findViewById(R.id.button_options);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        archiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Archive feature comming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }
}