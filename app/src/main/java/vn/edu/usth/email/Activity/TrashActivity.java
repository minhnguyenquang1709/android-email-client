package vn.edu.usth.email.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.email.R;

public class TrashActivity extends AppCompatActivity {

    private ListView listTrashEmails;
    private List<String> trashEmails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        listTrashEmails = findViewById(R.id.list_trash_emails);

        // Sample data for trash emails
        trashEmails = new ArrayList<>();
        trashEmails.add("Deleted Email 1");
        trashEmails.add("Deleted Email 2");
        trashEmails.add("Deleted Email 3");

        // Adapter to display the trash emails
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                trashEmails
        );

        listTrashEmails.setAdapter(adapter);
    }
}
