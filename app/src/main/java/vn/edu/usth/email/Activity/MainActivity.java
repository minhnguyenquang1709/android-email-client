package vn.edu.usth.email.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import vn.edu.usth.email.Fragment.MainToolbarFragment;
import vn.edu.usth.email.R;

public class MainActivity extends AppCompatActivity {
    MainToolbarFragment toolbar;
    Fragment toolbarFragment;

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

        toolbarFragment = (MainToolbarFragment) getSupportFragmentManager().findFragmentById(R.id.main_toolbar_fragment);
        View toolbarView = toolbarFragment.getView().findViewById(R.id.main_toolbar);

    }
}