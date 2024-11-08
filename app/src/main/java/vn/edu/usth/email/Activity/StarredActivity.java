package vn.edu.usth.email.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.email.R;

public class StarredActivity extends AppCompatActivity {

    private ListView listStarredEmails;
    private List<String> starredEmails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starred);

        listStarredEmails = findViewById(R.id.starred_recycle_view);

        // Sample data for starred emails
        starredEmails = new ArrayList<>();
        starredEmails.add("Starred Email 1");
        starredEmails.add("Starred Email 2");
        starredEmails.add("Starred Email 3");

        // Adapter to display the starred emails
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                starredEmails
        );

        listStarredEmails.setAdapter(adapter);
    }
}
