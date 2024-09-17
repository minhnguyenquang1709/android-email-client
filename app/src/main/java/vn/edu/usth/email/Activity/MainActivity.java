package vn.edu.usth.email.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import vn.edu.usth.email.Adapter.MainViewPagerAdapter;
import vn.edu.usth.email.Fragment.MainToolbarFragment;
import vn.edu.usth.email.R;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 mainViewPager;
    private MainViewPagerAdapter pagerAdapter;

    private static final int PAGE_COUNT = 3;


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
    }
}