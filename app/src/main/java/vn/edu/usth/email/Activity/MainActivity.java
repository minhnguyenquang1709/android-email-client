package vn.edu.usth.email.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import vn.edu.usth.email.Adapter.MainViewPagerAdapter;
import vn.edu.usth.email.R;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 mainViewPager;
    private MainViewPagerAdapter pagerAdapter;

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
    }
}