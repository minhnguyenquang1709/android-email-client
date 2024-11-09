package vn.edu.usth.email.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.api.services.gmail.Gmail;
import vn.edu.usth.email.Adapter.EmailAdapter;
import vn.edu.usth.email.Helper.GmailServiceHelper;
import vn.edu.usth.email.Helper.NavigationHelper;
import vn.edu.usth.email.Model.Email;
import vn.edu.usth.email.R;
import java.util.List;

public class StarredActivity extends AppCompatActivity {

    private static final String TAG = "StarredActivity";
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private Gmail service;
    private String userId;
    private String accessToken;
    private EmailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starred);

        recyclerView = findViewById(R.id.starred_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton menuButton = findViewById(R.id.icon_menu);

        userId = getIntent().getStringExtra("userId");
        accessToken = getIntent().getStringExtra("accessToken");

        if (userId == null || accessToken == null) {
            Log.e(TAG, "User ID or Access Token is missing.");
            return;
        }

        try {
            service = GmailServiceHelper.getService(accessToken);
            fetchEmails("label:starred");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Gmail service", e);
        }

        setupNavigation();

        // Set up menu button to open the drawer
        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        // Set up ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void fetchEmails(String query) {
        new Thread(() -> {
            try {
                List<Email> emailList = GmailServiceHelper.fetchEmails(service, userId, query);
                if (emailList != null && !emailList.isEmpty()) {
                    runOnUiThread(() -> {
                        adapter = new EmailAdapter(this, emailList, accessToken);
                        recyclerView.setAdapter(adapter);
                    });
                } else {
                    Log.i(TAG, "No starred emails found.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching starred emails", e);
            }
        }).start();
    }

    private void setupNavigation() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigationSelection(item);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigationSelection(item);
            return true;
        });
    }

    private void handleNavigationSelection(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.search) {
            NavigationHelper.navigateToActivity(this, SearchActivity.class, userId, accessToken);
        } else if (itemId == R.id.nav_sent) {
            NavigationHelper.navigateToActivity(this, SendActivity.class, userId, accessToken);
        } else if (itemId == R.id.Starred) {
            NavigationHelper.navigateToActivity(this, StarredActivity.class, userId, accessToken);
        } else if (itemId == R.id.trash) {
            NavigationHelper.navigateToActivity(this, TrashActivity.class, userId, accessToken);
        } else if (itemId == R.id.helpnfeedback) {
            startActivity(new Intent(this, HelpFeedbackActivity.class));
        }
    }

    private void handleBottomNavigationSelection(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.mail_icon) {
            NavigationHelper.navigateToActivity(this, StarredActivity.class, userId, accessToken);
        } else if (itemId == R.id.video_icon) {
            NavigationHelper.navigateToActivity(this, GeneralSettingActivity.class, userId, accessToken);
        }
    }
}
