package Domain;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
public class User {

    private String id;
    private String mail;
    private String username;
    private String userPictureUrl;
    private float height;
    private int weight;
    private int phoneNum;
    private Date birthDate;
    private List<Quest> quests;
    private Level level;
    private List<Achievement> achievements;
    private final static int QUEST_THRESHOLD = 2;
    private List<String> friends;
    private int totalSteps;
    private double totalMetersWalking;
    private double totalMetersRunning;

    public User(){}

    public User(String id, String mail, String username) {
        this.id = id;
        this.mail = mail;
        this.username = username;
        quests = new ArrayList<>();
        level = new Level();
        achievements = new ArrayList<>();
        friends = new ArrayList<>();
    }

    public User(String id, String mail, String username, String photo){
        this.id = id;
        this.mail = mail;
        this.username = username;
        quests = new ArrayList<>();
        level = new Level();
        achievements = new ArrayList<>();

        if(photo == null || photo.equals("DEFAULT")) {
            userPictureUrl = "http://icons.iconarchive.com/icons/custom-icon-design/flatastic-4/512/User-orange-icon.png";
        } else {
            userPictureUrl = photo;
        }

        friends = new ArrayList<>();
        quests.add(new Quest(QuestType.Steps, 100, null));
        quests.add(new Quest(QuestType.Walking, 2, UnitType.Kilometers));
    }


    public void setHeight(float height){this.height = height;}
    public void setWeight(int weight){this.weight = weight;}
    public void setPhoneNum(int phoneNum){this.phoneNum = phoneNum;}
    public void setBirthDate(Date birthDate){this.birthDate = birthDate;}
    public void setLevel(Level level){this.level = level;}
    public void setQuests(List<Quest> quests){this.quests = quests;}

    public String getId(){return id;}
    public String getMail(){return mail;}
    public String getUsername(){return username;}
    public String getImage() {return userPictureUrl;}
    public void setImage(String link) {userPictureUrl = link;}
    public float getHeight(){return height;}
    public int getWeight(){return weight;}
    public int getPhoneNum(){return phoneNum;}
    public Date getBirthDate(){return birthDate;}
    public List<Quest> getQuests(){return quests;}
    public Level getLevel(){return level;}
    public List<Achievement> getAchievements(){return achievements;}

    public void addFriend(String f) {friends.add(f);}
    public boolean isFriend(String f) {return friends.contains(f);}
    public List<String> getFriendsList() {return friends;}

    public void addQuest(Quest newQuest){
        if(quests.size() <= QUEST_THRESHOLD + level.getLevel()/10){
            quests.add(newQuest);
        }
    }

    public void addAchievement(Achievement achievement){
        if(!achievements.contains(achievement)){
            achievements.add(achievement);
        }
    }

    public static void save(User user){
        ObjectOutput out;
        try {
            File outFile = new File(Environment.getExternalStorageDirectory(), "appSaveState.data");
            out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(user);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static User load(){
        ObjectInput in;
        User ss = null;
        try {
            File outFile = new File(Environment.getExternalStorageDirectory(), "appSaveState.data");
            in = new ObjectInputStream(new FileInputStream(outFile));
            ss=(User) in.readObject();
            in.close();
        } catch (Exception e) {e.printStackTrace();}
        return ss;
    }

    public void setTotalMetersWalking(double totalMeters) {
        this.totalMetersWalking = totalMeters;
    }

    public double getTotalMetersWalking() {
        return totalMetersWalking;
    }

    public void setTotalMetersRunning(double totalMetersRunning) {
        this.totalMetersRunning = totalMetersRunning;
    }

    public double getTotalMetersRunning() {
        return totalMetersRunning;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public int getTotalSteps() {
        return totalSteps;
    }
}
