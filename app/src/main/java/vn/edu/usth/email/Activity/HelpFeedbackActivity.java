package vn.edu.usth.email.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import vn.edu.usth.email.R;

public class HelpFeedbackActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_feedback);

        Button btnSendFeedback = findViewById(R.id.btn_send_feedback);

        btnSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });
    }

    private void sendFeedback() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:support@usth.edu.vn")); // Replace with actual support email
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for the Email App");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Write your feedback here...");

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
    }
}
