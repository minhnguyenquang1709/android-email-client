package vn.edu.usth.email.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import vn.edu.usth.email.Adapter.MainViewPagerAdapter;
import vn.edu.usth.email.R;

public class    MainActivity extends AppCompatActivity {
    private Toolbar appBar;
    private TabLayout tabLayout;
    private ViewPager2 mainViewPager;
    private MainViewPagerAdapter pagerAdapter;
    private ImageButton searchBtn;
    private ImageButton settingsBtn;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mainViewPager = findViewById(R.id.main_view_pager);
        pagerAdapter = new MainViewPagerAdapter(this);
        mainViewPager.setOffscreenPageLimit(3);
        mainViewPager.setAdapter(pagerAdapter);

        // use TabLayoutMediator to link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, mainViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                // setup icon for each tab
                switch (position){
                    case 0:
                        tab.setIcon(R.drawable.folder_icon);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.inbox_icon);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.star_icon);
                        break;
                    default: tab.setText("Tab");
                }
            }
        }).attach();
        tabLayout.setTabIconTintResource(R.color.tab_icon_color); // setup color when selected and not selected for icon

        // setup the AppBar
        appBar = (Toolbar) findViewById(R.id.appbar_main);
        setSupportActionBar(appBar);

        // setup button to open settings
        settingsBtn = findViewById(R.id.settings_button);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GeneralSettingActivity.class);
                startActivity(intent);
            }
        });

        // setup button to open Search
        searchBtn = findViewById(R.id.search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        // setup Floating Button for writing new mail
        floatingActionButton = findViewById(R.id.write_email);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Floating Action Button", "Write new mail");
                Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
                startActivity(intent);
            }
        });

        // toggle FAB visibility for each page selected
        mainViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position){
                switch (position){
                    case 0:
                        floatingActionButton.setVisibility(View.GONE);
                        break;
                    case 1:
                        floatingActionButton.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        floatingActionButton.setVisibility(View.VISIBLE);
                        break;
                    default:
                        floatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}