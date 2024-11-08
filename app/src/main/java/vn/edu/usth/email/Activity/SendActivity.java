package vn.edu.usth.email.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.email.R;

public class SendActivity extends AppCompatActivity {

    private ListView listSentEmails;
    private List<String> sentEmails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        listSentEmails = findViewById(R.id.sent_recycle_view);

        // Sample data for sent emails
        sentEmails = new ArrayList<>();
        sentEmails.add("Sent Email 1");
        sentEmails.add("Sent Email 2");
        sentEmails.add("Sent Email 3");

        // Adapter to display the sent emails
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                sentEmails
        );

        listSentEmails.setAdapter(adapter);
    }
}
