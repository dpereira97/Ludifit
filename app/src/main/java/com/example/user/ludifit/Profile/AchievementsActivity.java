package com.example.user.ludifit.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.user.ludifit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Domain.Achievement;
import Domain.AchievementCategory;

public class AchievementsActivity extends AppCompatActivity {
    ListView listView;
    private ArrayList<String> achNames = new ArrayList<>();
    private ArrayList<String> achGot = new ArrayList<>();

    private final FirebaseDatabase firebaseReference = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private ArrayList<Achievement> achievements = new ArrayList<>();
    private ArrayList<Integer> listIds = new ArrayList<>();
    private String[] achNamesArray;
    private String[] achDesc;
    private int[] achImgs;
    private ListAdapter adapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achivements);

        String uid = mFirebaseAuth.getCurrentUser().getUid();

        DatabaseReference usersRef = firebaseReference.getReference("Users/" + uid + "/achievements");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Achievement ac;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ac = snapshot.getValue(Achievement.class);
                    achievements.add(ac);
                }
                for (Achievement a : achievements) {
                    achNames.add(a.getType().toString());
                    achGot.add(a.getDescription());

                    if(a.getType().equals(AchievementCategory.Steps)) {
                        listIds.add(R.drawable.steps);
                    }
                    else if(a.getType().equals(AchievementCategory.Walking)) {
                        listIds.add(R.drawable.walk);
                    } else {
                        listIds.add(R.drawable.running);
                    }
                }

                achNamesArray = achNames.toArray(new String[achNames.size()]);

                achDesc = achGot.toArray(new String[achGot.size()]);


                achImgs = toArray(listIds);

                listView = findViewById(R.id.listviewLayout);

                adapt = new ListAdapter(getApplicationContext(), achNamesArray,achDesc,achImgs);
                listView.setAdapter(adapt);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        ImageView img = findViewById(R.id.backbtn);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    public int[] toArray(List<Integer> list) {
        int[] ret = new int[ list.size() ];
        int i = 0;
        for(Iterator<Integer> it = list.iterator();
            it.hasNext();
            ret[i++] = it.next() );
        return ret;
    }

    private void back() {
        onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
}