package com.example.user.ludifit.MainScreen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.user.ludifit.R;

public class QuestCompletePopUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.80),(int)(height*0.20));
        setContentView(R.layout.activity_quest_complete);
        FrameLayout fl = findViewById(R.id.complete_layout);
        fl.getBackground().setAlpha(150);

        TextView text = findViewById(R.id.quest_complete_text);
        int xp = getIntent().getIntExtra("xp",0);
        text.setText("You have gained " + xp + " experience!");
        Button btn = findViewById(R.id.quest_complete_button);

        btn.setOnClickListener(new View.OnClickListener() {
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
