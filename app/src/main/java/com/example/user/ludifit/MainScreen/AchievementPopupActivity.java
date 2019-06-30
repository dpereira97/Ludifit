package com.example.user.ludifit.MainScreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.user.ludifit.R;

public class AchievementPopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int) (width * 0.80), (int) (height * 0.30));
        setContentView(R.layout.activity_achievement_popup);
        FrameLayout frameLayout = findViewById(R.id.achiv_complete_layout);
        frameLayout.getBackground().setAlpha(150);

        TextView text = findViewById(R.id.achievement_text);
        String description = getIntent().getStringExtra("description");
        text.setText("You got the achievement: " + description + "!");
        Button button = findViewById(R.id.achievement_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, R.anim.exit_to_right);
        super.onPause();
    }
}