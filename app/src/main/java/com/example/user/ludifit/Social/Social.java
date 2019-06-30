package com.example.user.ludifit.Social;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.user.ludifit.MainScreen.MainActivity;
import com.example.user.ludifit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Domain.Achievement;
import Domain.Group;
import Domain.Level;
import Domain.Quest;
import Domain.QuestType;
import Domain.UnitType;
import Domain.User;

public class Social extends AppCompatActivity implements GestureDetector.OnGestureListener, PopupMenu.OnMenuItemClickListener, SearchView.OnQueryTextListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private GestureDetector gestureDetector;
    private static final String TAG = "SocialActivity";
    private User currentUser;

    //friends list
//    private ArrayList<String> friendsIds = new ArrayList<>();
//    private ArrayList<String> friendNames = new ArrayList<>();
//    private ArrayList<String> friendImageUrls = new ArrayList<>();

    //groups list
    private ArrayList<String> groupNames = new ArrayList<>();
    private ArrayList<String> groupImageUrls = new ArrayList<>();

    private HashMap<String, User> userList = new HashMap<>();

    //for testing purposes
    private User u;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mFirebaseAuth;

    //variables to get user values
    private String username = "";
    private String mail = "";
    private String photoRef = "";
    private String userPictureUrl = "";
    private float height;
    private int weight;
    private int phoneNum;
    private Date birthDate;
    private List<Quest> quests;
    private Level level;
    private List<Achievement> achievements;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentUser = MainActivity.getCurrentUser();

        super.onCreate(savedInstanceState);

        gestureDetector = new GestureDetector(this);

        setContentView(R.layout.activity_social);
        getInfo();

        initGroupRecyclerView();
    }

    private void getInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectUsers((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void collectUsers(Map<String,Object> users) {

        for (Map.Entry<String, Object> entry : users.entrySet()){
           HashMap<String, Object> userMap = (HashMap<String, Object>) entry.getValue();

           String usid = (String)userMap.get("id");
           String usmail = (String)userMap.get("mail");
           String usun = (String)userMap.get("username");
           String usphoto = (String)userMap.get("image");

           User u = new User(usid, usmail, usun, usphoto);

           userList.put(u.getId(), u);
        }
        initFriendsRecyclerView();
    }

    private void initFriendsRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.horizontal_friends_list);
        recyclerView.setLayoutManager(layoutManager);
        FriendsRecyclerViewAdapter adapter = new FriendsRecyclerViewAdapter(this, userList, this);
        recyclerView.setAdapter(adapter);
    }

    private void initGroupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.horizontal_groups_list);
        recyclerView.setLayoutManager(layoutManager);
        GroupsRecyclerViewAdapter adapter = new GroupsRecyclerViewAdapter(this, groupNames, groupImageUrls, this);
        recyclerView.setAdapter(adapter);
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
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean result = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

        //Check if it's a left or right swipe
        if(Math.abs(diffX) > Math.abs(diffY)){
            if(Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                if(diffX > 0) {
                    onSwipeRight();
                }
                result = true;
            }
        }

        return result;
    }

    private void onSwipeRight() {
        onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    public void showFriendsPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.social_friends_popup_menu);
        popup.show();
    }

    public void showGroupsPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.social_groups_popup_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.check_profile:
                return true;
            case R.id.add_friend:
                if(!currentUser.isFriend(u.getId())) {
                    System.out.println("\n\nfriend added\n\n");

                    u.addFriend(currentUser.getId());
                    currentUser.addFriend(u.getId());

                    DatabaseReference usersRef = database.getReference("Users/" + u.getId() + "/friends/");
                    usersRef.setValue(u.getFriendsList());
                    usersRef = database.getReference("Users/" + currentUser.getId() + "/friends/");
                    usersRef.setValue(currentUser.getFriendsList());
                } else {
                    Toast.makeText(this, "That user's already your friend",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.challenge_friend:
                return true;
            case R.id.challenge_group:
                return true;
            case R.id.check_members:
                return true;
            default:
                return false;
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public void setU(String u) {
        this.u = userList.get(u);
    }
}
