package com.example.user.ludifit.Profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.ludifit.Authentication.AuthenticationActivity;
import com.example.user.ludifit.MainScreen.MainActivity;
import com.example.user.ludifit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import Domain.User;

public class ProfileActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private GestureDetector gestureDetector;
    private final FirebaseDatabase firebaseReference = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector(this);
        setContentView(R.layout.activity_profile);

        String uid = mFirebaseAuth.getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        LinearLayout achiv = findViewById(R.id.achivclick);


        ImageView imageView = findViewById(R.id.profilePic);

        User u = MainActivity.getCurrentUser();

        TextView t = findViewById(R.id.profile_user_name);
        TextView level = findViewById(R.id.profile_level);
        TextView friends = findViewById(R.id.profile_num_friends);
        TextView achievements = findViewById(R.id.profile_num_achievements);
        t.setText(u.getUsername());
        level.setText("" + u.getLevel().getLevel());
       // friends.setText("" + u.getFriendsList().length);
        achievements.setText("" + u.getAchievements().size());

        EditText mail = findViewById(R.id.email_field);
        mail.setText(u.getMail());
        EditText heightWeight = findViewById(R.id.assets_field);
        heightWeight.setText("H:"+u.getHeight() + " W:"+u.getWeight());
        EditText phone = findViewById(R.id.cell_field);
        phone.setText("+351 "+u.getPhoneNum());


        ImageView img = findViewById(R.id.profilePic);
        Glide.with(this).asBitmap().load(u.getImage()).into(img);

        final EditText edt_email =  findViewById(R.id.email_field);
        final EditText edt_cell =  findViewById(R.id.cell_field);
        final EditText edt_date =  findViewById(R.id.date_field);
        final EditText edt_assets = findViewById(R.id.assets_field);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        if(sharedPref != null){
            if(sharedPref.contains("email")){
                edt_email.setText(sharedPref.getString("email",edt_email.getText().toString()));
            }

            if(sharedPref.contains("assets")){
                edt_assets.setText(sharedPref.getString("assets",edt_assets.getText().toString()));
            }

            if(sharedPref.contains("cell")){
                edt_cell.setText(sharedPref.getString("cell",edt_cell.getText().toString()));
            }

            if(sharedPref.contains("date")){
                edt_date.setText(sharedPref.getString("date",edt_date.getText().toString()));
            }
        }

        final Calendar myCalendar = Calendar.getInstance();


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                edt_date.setText(sdf.format(myCalendar.getTime()));
            }

        };

        edt_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        edt_assets.setEnabled(false);
        edt_email.setEnabled(false);
        edt_date.setEnabled(false);
        edt_cell.setEnabled(false);

        final Button btn_edit= findViewById(R.id.btn_edit);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_edit.getText().toString().equalsIgnoreCase("edit")){
                    edt_assets.setEnabled(true);
                    edt_email.setEnabled(true);
                    edt_date.setEnabled(true);
                    edt_cell.setEnabled(true);

                    btn_edit.setText("save");
                }else{
                    edt_assets.setEnabled(false);
                    edt_email.setEnabled(false);
                    edt_date.setEnabled(false);
                    edt_cell.setEnabled(false);

                    saveInfo(edt_email.getText().toString(),edt_cell.getText().toString(),edt_date.getText().toString(),edt_assets.getText().toString());
                    btn_edit.setText("edit");
                    Toast.makeText(ProfileActivity.this,"SAVED",Toast.LENGTH_LONG).show();
                }


            }
        });

        achiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,AchievementsActivity.class);
                startActivity(intent);
            }
        });


        DatabaseReference usersAchivRef = firebaseReference.getReference("Users/" + uid + "/achievements");
        usersAchivRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    count ++;
                }

                if(count > 0) {
                    TextView achievements = findViewById(R.id.profile_num_achievements);
                    achievements.setText(""+count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
        public boolean onCreateOptionsMenu(Menu menu) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_for_signout, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            switch(item.getItemId()){
                case R.id.menu_Logout:

                    FirebaseAuth.getInstance().signOut();
                    finish();
                    startActivity(new Intent(this,AuthenticationActivity.class));
                    break;
            }
            return true;
    }

    private void saveInfo(String s, String s1, String s2, String s3) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("email",s);
        editor.putString("cell",s1);
        editor.putString("date",s2);
        editor.putString("assets",s3);

        editor.apply();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
    }

    @Override
    public boolean onDown(MotionEvent e) { return false; }

    @Override
    public void onShowPress(MotionEvent e) { }

    @Override
    public boolean onSingleTapUp(MotionEvent e) { return false; }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }

    @Override
    public void onLongPress(MotionEvent e) { }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return gestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean result = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

        //Check if it's a left or right swipe
        if(Math.abs(diffX) > Math.abs(diffY)){
            if(Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                if(diffX < 0) {
                    onSwipeLeft();
                }
                result = true;
            }
        }

        return result;
    }

    private void onSwipeLeft() {
        onBackPressed();
        overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
    }
}