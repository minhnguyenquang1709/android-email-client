package vn.edu.usth.email.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.email.R;

public class TrashActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton menuButton = findViewById(R.id.icon_menu);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.search) {
                    startActivity(new Intent(TrashActivity.this, SearchActivity.class));
                }else if (itemId == R.id.Starred) {
                    startActivity(new Intent(TrashActivity.this, StarredActivity.class));
                } else if (itemId == R.id.nav_sent) {
                    startActivity(new Intent(TrashActivity.this, SendActivity.class));
                } else if (itemId == R.id.trash) {
                    startActivity(new Intent(TrashActivity.this, TrashActivity.class));
                } else if (itemId == R.id.helpnfeedback) {
                    startActivity(new Intent(TrashActivity.this, HelpFeedbackActivity.class));
                }

                // Close the drawer after an item is clicked
                drawerLayout.closeDrawers();
                return true;
            }
        });
        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));


    }
}
