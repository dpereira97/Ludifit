package Domain;

import android.graphics.Bitmap;


public class Achievement {

    private AchievementCategory type;
    private String description;
    private Bitmap image;
    private int goal, id;

    public Achievement() {}

    public Achievement(int id, AchievementCategory type, String description, Bitmap image, int goal){
        this.id = id;
        this.type = type;
        this.description = description;
        this.image = image;
        this.goal = goal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean achieved(int val){
        return val >= goal;
    }

    public void setType(AchievementCategory type) {
        this.type = type;
    }

    public AchievementCategory getType() {
        return type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getGoal() {
        return goal;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }
}
