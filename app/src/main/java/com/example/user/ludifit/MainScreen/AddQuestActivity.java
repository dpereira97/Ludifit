package com.example.user.ludifit.MainScreen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.user.ludifit.R;

import org.w3c.dom.Text;

import Domain.QuestType;

public class AddQuestActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.80),(int)(height*0.40));

        setContentView(R.layout.activity_add_quest);
        FrameLayout fl = findViewById(R.id.quest_layout);
        fl.getBackground().setAlpha(150);
        setQuestTypes();

        final Button cancel = findViewById(R.id.cancel_button);
        Button submit = findViewById(R.id.create_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void setQuestTypes(){
        Spinner dropdown = findViewById(R.id.quest_types);
        String[] items = new String[]{"Steps", "Walking", "Running"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        Spinner distanceTypes = findViewById(R.id.distance_types);
        String[] items2 = new String[]{"Meters", "Kilometers"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        distanceTypes.setAdapter(adapter2);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        TextView text = findViewById(R.id.quest_description);
        TextView num = findViewById(R.id.quest_number);
        num.setTextColor(Color.WHITE);
        Spinner sp = findViewById(R.id.distance_types);

        switch (position) {
            case 0:
                text.setText("Number of steps:");
                sp.setVisibility(View.INVISIBLE);
                break;
            case 1:
                text.setText("Distance to walk:");
                sp.setVisibility(View.VISIBLE);
                break;
            case 2:
                text.setText("Distance to run:");
                sp.setVisibility(View.VISIBLE);
                break;

        }
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, R.anim.exit_to_down);
        super.onPause();
        cancel();
    }

    public void cancel(){
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    public void submit(){
        Intent intent = new Intent();
        Spinner s = findViewById(R.id.quest_types);
        Spinner s2 = findViewById(R.id.distance_types);
        String type = s.getSelectedItem().toString();
        String unitType = s2.getSelectedItem().toString();
        TextView t = findViewById(R.id.quest_number);
        int goal = 0;
        try {
            goal = Integer.parseInt(t.getText().toString());
        }catch (Exception e){
            cancel();
        }
        intent.putExtra("type", type);
        intent.putExtra("goal", goal);
        intent.putExtra("unit", unitType);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
