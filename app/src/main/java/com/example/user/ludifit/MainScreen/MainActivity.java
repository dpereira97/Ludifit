package com.example.user.ludifit.MainScreen;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.user.ludifit.Circles.MainCirclesFragment;
import com.example.user.ludifit.Profile.AchievementsUtility;
import com.example.user.ludifit.Profile.ProfileActivity;
import com.example.user.ludifit.R;
import com.example.user.ludifit.Social.Social;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Domain.Achievement;
import Domain.Level;
import Domain.Quest;
import Domain.QuestType;
import Domain.UnitType;
import Domain.User;


public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, AddQuestButtonFragment.addQuestInterface,
        UserQuestFragment.QuestComplete {

    private static boolean active = false;

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    private GestureDetector gestureDetector;
    private static User user;
    private int questId;
    private boolean isServiceStopped;
    private String countedSteps;
    private String detectedStep;
    final FirebaseDatabase firebaseReference = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    ProgressBar loadUser;

    //variables to get user values
    private String uid;
    private String username = "";
    private String mail = "";
    private String photoRef = "";
    private String userPictureUrl = "";
    private int newQuestId = 0;
    private float height;
    private int weight;
    private int phoneNum;
    private Date birthDate; // profile?
    private List<Quest> quests;
    private List<Integer> fragmentIds;
    private Level level;
    private List<Achievement> achievements; // profile?
    private FirebaseStorage storage; // profile?
    private StorageReference storageReference; // profile?
    private int previousSteps;
    private boolean firstTime = true;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private QuestType questType;

    private List<User> friendsList;

    private HashMap<Integer, Quest> activeFragmentsQuests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        uid = mFirebaseAuth.getCurrentUser().getUid();
        loadUser = findViewById(R.id.progress_load_user);
        loadUser.setVisibility(View.VISIBLE);
        activeFragmentsQuests = new HashMap<>();
        fragmentIds = new ArrayList<>();

        FirebaseApp.initializeApp(this);
        DatabaseReference dr = firebaseReference.getReference("Users");

        dr.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (firstTime) {
                    mail = dataSnapshot.child("mail").getValue(String.class);
                    username = dataSnapshot.child("username").getValue(String.class);
                    userPictureUrl = dataSnapshot.child("image").getValue(String.class);
                    try {
                        height = dataSnapshot.child("height").getValue(float.class);
                    } catch (Exception e) {
                    }
                    try {
                        weight = dataSnapshot.child("weight").getValue(int.class);
                    } catch (Exception e) {
                    }
                    try {
                        phoneNum = dataSnapshot.child("phoneNum").getValue(int.class);
                    } catch (Exception e) {
                    }
                    //quests = data.child("quests").getValue(List.class);
                    level = new Level();
                    try {
                        level.setLevel(dataSnapshot.child("level/level").getValue(int.class));
                    } catch (Exception e) {
                    }
                    try {
                        level.setCurrXp(dataSnapshot.child("level/currXp").getValue(int.class));
                    } catch (Exception e) {
                    }
                    try {
                        level.setNeededXp(dataSnapshot.child("level/neededXp").getValue(int.class));
                    } catch (Exception e) {
                    }
                    try {
                        photoRef = dataSnapshot.child("image").getValue(String.class);
                    } catch (Exception e) {
                    }

                    quests = new ArrayList<>();

                    /* TODO da maneira padeira */

                    while (dataSnapshot.child("quests/" + newQuestId).getValue(Quest.class) != null) {

                            QuestType type = QuestType.valueOf(dataSnapshot.child("quests/" + newQuestId + "/type").getValue(String.class));
                            int goal = 0, done = 0; int id = 0; boolean isDone = false;
                            UnitType uType = null;
                            try {
                                goal = dataSnapshot.child("quests/" + newQuestId + "/goal").getValue(int.class);
                                done = dataSnapshot.child("quests/" + newQuestId + "/done").getValue(int.class);
                                isDone = dataSnapshot.child("quests/" + newQuestId + "/isDone").getValue(boolean.class);
                            } catch (Exception e) {
                            }
                            try {
                                uType = UnitType.valueOf(dataSnapshot.child("quests/" + newQuestId + "/unitType").getValue(String.class));
                            } catch (Exception e) {
                            }
                            Quest q = new Quest(type, goal, uType);
                            q.setDone(done);
                            q.setIsDone(isDone);
                            q.setId(newQuestId);
                            quests.add(q);

                        newQuestId++;
                    }
                    user = new User(uid, mail, username, photoRef);
                    user.setHeight(height);
                    user.setWeight(weight);
                    user.setPhoneNum(phoneNum);
                    user.setLevel(level);
                    user.setQuests(quests);
                    if (active) {
                        if (firstTime) {
                            addUserFragment();
                            addUserQuests();
                        }
                    }
                    loadUser.setVisibility(View.GONE);
                    firstTime = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MainCirclesFragment mc = new MainCirclesFragment();
        AddQuestButtonFragment aqb = new AddQuestButtonFragment();
        ft.add(R.id.add_quest_frame, aqb);
        ft.add(R.id.circles_frame, mc);
        ft.commit();

        gestureDetector = new GestureDetector(this);
        isServiceStopped = true;
//        AchievementsUtility.achievementsDatabase(firebaseReference);
    }

    public static User getCurrentUser() {
        return user;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FrameLayout fl = findViewById(R.id.darker_background);
        fl.getForeground().setAlpha(0);
    }

    /*
     * Method called by fragment to terminate it, giving xp to the user
     */
    public void completeQuest(int xp, int id) {
        Quest q = user.getQuests().get(id);
        user.getLevel().addXp(xp);
        q.addDone(q.getGoal() + 10);
        Level l = user.getLevel();
        refreshUserLevel(l.getLevel(), l.getCurrXp(), l.getNeededXp());
        completeQuestPopup(xp);
        stopService(new Intent(this, StepCountingService.class));
        isServiceStopped = true;

        QuestType qt = q.getType();
        if (qt.equals(QuestType.Steps)) {
            user.setTotalSteps(user.getTotalSteps() + (int) q.getGoal());
        } else if (qt.equals(QuestType.Walking)) {
            user.setTotalMetersWalking(user.getTotalMetersWalking() + q.getGoal());
        } else if (qt.equals(QuestType.Running)) {
            user.setTotalMetersRunning(user.getTotalMetersRunning() + q.getGoal());
        }
        getsAchievement(qt);

    }

    /**
     * Method that checks if the user is elegable for an achievement
     * adds to list of achievements, shows popup and displays on the achievements screen if so
     * if not, it just continues with the normal execution plan
     */
    private void getsAchievement(QuestType qt) {
        DatabaseReference ref = firebaseDatabase.getReference("Achievements");
        this.questType = qt;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Achievement result;

                if (questType.equals(QuestType.Steps)) {
                    for (DataSnapshot ds : dataSnapshot.child("steps").getChildren()) {
                        result = ds.getValue(Achievement.class);
                        if (user.getTotalSteps() >= result.getGoal()) {
                            user.addAchievement(result);
                            if(!isActivityRunning(QuestCompletePopUpActivity.class)) {
                                achievementPopUp(result);
                            }
                        }
                    }
                } else if (questType.equals(QuestType.Walking)) {
                    for (DataSnapshot ds : dataSnapshot.child("walking").getChildren()) {
                        result = ds.getValue(Achievement.class);
                        if (user.getTotalMetersWalking() >= result.getGoal() && !result.getDescription().equals("Walked 1 kilometer")){
                            user.addAchievement(result);
                            achievementPopUp(result);
                        }

                        if (user.getTotalMetersWalking() * 0.001 > 1 && result.getDescription().equals("Walked 1 kilometer")) {
                            user.addAchievement(result);
                            achievementPopUp(result);
                        }
                    }
                } else if (questType.equals(QuestType.Running)) {
                    for (DataSnapshot ds : dataSnapshot.child("running").getChildren()) {
                        result = ds.getValue(Achievement.class);

                        if (user.getTotalMetersRunning() >= result.getGoal() && !!result.getDescription().equals("Ran 1 kilometer")) {
                            user.addAchievement(result);
                            achievementPopUp(result);
                        }

                        if (user.getTotalMetersRunning() * 0.001 > 1 && result.getDescription().equals("Ran 1 kilometer")) {
                            user.addAchievement(result);
                            achievementPopUp(result);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void achievementPopUp(Achievement achievement) {
        Intent myIntent = new Intent(this, AchievementPopupActivity.class);
        FrameLayout fl = findViewById(R.id.darker_background);
        fl.getForeground().setAlpha(150);
        myIntent.putExtra("description", achievement.getDescription());
        startActivity(myIntent);
        overridePendingTransition(R.anim.enter_from_left, 0);
        saveAchievementDatabase();
    }

    /**
     * if the user got an achievement, add it to the user in the database
     * also add the achievement screen to database (?)
     */
    private void saveAchievementDatabase() {
        DatabaseReference usersRef = firebaseReference.getReference("Users/" + uid + "/achievements");
        usersRef.setValue(user.getAchievements());
    }

    protected Boolean isActivityRunning(Class activityClass) {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }

    private void completeQuestPopup(int xp) {
        Intent myIntent = new Intent(this, QuestCompletePopUpActivity.class);
        FrameLayout fl = findViewById(R.id.darker_background);
        fl.getForeground().setAlpha(150);
        myIntent.putExtra("xp", xp);
        startActivity(myIntent);
        overridePendingTransition(R.anim.enter_from_left, 0);
        completeQuestInfoToDatabase();
    }

    private void completeQuestInfoToDatabase() {
        DatabaseReference usersRef = firebaseReference.getReference("Users/" + uid + "/level");
        DatabaseReference usersRef2 = firebaseReference.getReference("Users/" + uid + "/quests");
        usersRef.setValue(user.getLevel());
        usersRef2.setValue(user.getQuests());
    }

    private void addUserQuests() {
        List<Quest> quests = user.getQuests();
        for (Quest q : quests) {
            if (!q.getIsDone()) {
                newQuest(q);
            }
        }
    }

    private void newQuest(Quest quest) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        UserQuestFragment uq = new UserQuestFragment();
        // manter array list com id's dos fragmentos ativos
        // adicionar ao array list aqui
        // apagar do arraylist no metodo remove quest
        fragmentIds.add(quest.getId());

        Bundle args = new Bundle();
        args.putString("name", quest.getType().name());
        try {
            args.putString("unit", quest.getUnitType().name());
        } catch (Exception e) {
            args.putString("unit", "nothing");
        }
        args.putDouble("done", quest.getDone());
        args.putDouble("obj", quest.getGoal());
        args.putInt("xpGiven", quest.completeQuestXp());
        args.putInt("id", quest.getId());
        uq.setArguments(args);
        ft.add(R.id.user_quests_frame, uq, String.valueOf(quest.getId()));
        ft.commitAllowingStateLoss();
        startService(new Intent(this, StepCountingService.class));
        registerReceiver(broadcastReceiver, new IntentFilter(StepCountingService.BROADCAST_ACTION));
        isServiceStopped = false;

    }

    private void addUserFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        TopUserFragment uq = new TopUserFragment();
        Bundle args = new Bundle();
        args.putString("name", user.getUsername());
        args.putInt("level", user.getLevel().getLevel());
        args.putInt("currXp", user.getLevel().getCurrXp());
        args.putInt("neededXp", user.getLevel().getNeededXp());
        uq.setArguments(args);
        ft.add(R.id.top_user_frame, uq, "user_profile");
        ft.commit();
    }

    private void refreshUserLevel(int newLevel, int currXp, int neededXp) {
        TopUserFragment up = (TopUserFragment) getSupportFragmentManager().findFragmentByTag("user_profile");
        up.changeLevel(newLevel, currXp, neededXp);
    }

    public void startAddQuest() {
        Intent myIntent = new Intent(this, AddQuestActivity.class);
        FrameLayout fl = findViewById(R.id.darker_background);
        fl.getForeground().setAlpha(150);
        startActivityForResult(myIntent, 2);
        overridePendingTransition(R.anim.enter_from_down, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        String t = data.getStringExtra("type");
        int goal = data.getIntExtra("goal", 0);
        String ut = data.getStringExtra("unit");
        if (goal != 0 && t != null) {
            QuestType type = QuestType.valueOf(t);
            UnitType unit = UnitType.valueOf(ut);
            Quest quest = new Quest(type, goal, unit);
            quest.setId(newQuestId++);
            user.addQuest(quest);

            newQuest(quest);
            addQuestToDatabase();
            if (quest.getType().equals(QuestType.Steps)) {   // HEREEEEE
                startService(new Intent(this, StepCountingService.class));
                registerReceiver(broadcastReceiver, new IntentFilter(StepCountingService.BROADCAST_ACTION));
                isServiceStopped = false;
            }

        }
    }

    private void addQuestToDatabase() {
        DatabaseReference usersRef = firebaseReference.getReference("Users/" + uid + "/quests/");
        usersRef.setValue(user.getQuests());
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

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
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
                result = true;
            }
        }

        return result;
    }

    private void onSwipeRight() {
        Intent myIntent = new Intent(this, ProfileActivity.class);
        startActivity(myIntent);
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    private void onSwipeLeft() {
        Intent myIntent = new Intent(this, Social.class);
        startActivity(myIntent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateViews(intent);
        }
    };

    private void updateViews(Intent intent) {
        detectedStep = intent.getStringExtra("Detected_Step");
        //Quest q = user.getQuests().get(user.getQuests().size() - 1);
        if (user.getQuests().size() == 0) return;
        for (Quest q : user.getQuests()) {
            if (!q.getIsDone()) {
                updateQuest(q, q.getId());
            }
        }
    }

    private void updateQuest(Quest q, int idFrag) {
        UserQuestFragment u = (UserQuestFragment) getSupportFragmentManager().findFragmentByTag(String.valueOf(idFrag));
        if (u != null) {
            if (q.getType().equals(QuestType.Steps)) {
                while (previousSteps != Integer.parseInt(detectedStep) && q.getDone() != Integer.parseInt(detectedStep) && !q.getIsDone()) {
                    u.addToCurrent(1);
                    q.addDone(1);
                }
                previousSteps = Integer.parseInt(detectedStep);
            }
            if ((q.getType().equals(QuestType.Walking) && q.getUnitType().equals(UnitType.Meters)) || (q.getType().equals(QuestType.Running) && q.getUnitType().equals(UnitType.Meters))) {
                while (previousSteps != Integer.parseInt(detectedStep) && q.getDone() < 0.762 * Integer.parseInt(detectedStep) && !q.getIsDone()) {
                    u.addToCurrent(0.762);
                    q.addDone(0.762);
                }
                previousSteps = Integer.parseInt(detectedStep);
            }
            if ((q.getType().equals(QuestType.Walking) && q.getUnitType().equals(UnitType.Kilometers)) || (q.getType().equals(QuestType.Running) && q.getUnitType().equals(UnitType.Kilometers))) {
                while (previousSteps != Integer.parseInt(detectedStep) && q.getDone() < 0.000762 * Integer.parseInt(detectedStep) && !q.getIsDone()) {
                    u.addToCurrent(0.000762);
                    q.addDone(0.000762);
                }
                previousSteps = Integer.parseInt(detectedStep);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
}