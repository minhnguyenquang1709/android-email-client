package vn.edu.usth.email.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import vn.edu.usth.email.R;

public class GeneralSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_setting);

        // Find the back button and set up the click listener
        ImageButton backButton = findViewById(R.id.button_back);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Close the current activity and return to the previous one
                }
            });
        } else {
            // Log or show an error if the button is not found (you can add logging here)
            throw new RuntimeException("Back button not found in the layout.");
        }
    }
}
